#!/bin/bash

cd /home/ubuntu/cicd

# 환경변수 설정
APP_NAME="assu"
TARGET=""
OLD=""

# NGINX 설정 관련
NGINX_CONF_PATH="/etc/nginx"
BLUE_CONF="blue.conf"
GREEN_CONF="green.conf"
DEFAULT_CONF="nginx.conf"
MAX_RETRIES=3
RETRY_SLEEP_SEC=5
HEALTH_CHECK_PORT=""

# 활성화된 서비스 확인 및 스위칭 대상 결정
determine_target() {
  if docker compose -f docker-compose.yml ps | grep -q "app-blue.*Up"; then
    TARGET="green"
    OLD="blue"
    HEALTH_CHECK_PORT="8081"
  elif docker compose -f docker-compose.yml ps | grep -q "app-green.*Up"; then
    TARGET="blue"
    OLD="green"
    HEALTH_CHECK_PORT="8080"
  else
    TARGET="blue"  # 첫 실행 시 기본값
    OLD="none"
    HEALTH_CHECK_PORT="8080"
  fi

  echo "TARGET: $TARGET"
  echo "OLD: $OLD"
}

# docker ps 기반 헬스체크: 컨테이너 상태가 Up이면 성공으로 판단
health_check() {
  local RETRIES=0
    local CONTAINER_NAME="app-$TARGET"

    echo "Starting docker-ps based health check for '$CONTAINER_NAME'..."

    # 초기 대기 (컨테이너 기동 시간 확보)
    sleep 10

    while [ $RETRIES -lt $MAX_RETRIES ]; do
      echo "Attempt $((RETRIES + 1)) of $MAX_RETRIES: checking docker ps status..."

      # docker ps에서 해당 컨테이너의 상태 문자열을 가져옴 (예: "Up 10 seconds")
      local STATUS
      STATUS=$(docker ps --filter "name=^${CONTAINER_NAME}$" --format '{{.Status}}' || true)

      if [ -n "$STATUS" ] && [[ "$STATUS" == Up* ]]; then
        echo "Health check succeeded! '$CONTAINER_NAME' is Up. (status: $STATUS)"
        return 0
      else
        # 상태 디버깅용 출력
        docker compose -f docker-compose.yml ps || true
        docker logs --tail=50 "$CONTAINER_NAME" 2>/dev/null || true
        echo "Current status: '${STATUS:-N/A}'. Retrying in ${RETRY_SLEEP_SEC}s..."
        sleep "$RETRY_SLEEP_SEC"
      fi

      RETRIES=$((RETRIES + 1))
    done

    echo "Health check failed after $MAX_RETRIES attempts."
    return 1
}

# NGINX 설정 스위칭 함수
switch_nginx_conf() {
  if [ "$TARGET" = "blue" ]; then
    sudo cp "${NGINX_CONF_PATH}/${BLUE_CONF}" "${NGINX_CONF_PATH}/${DEFAULT_CONF}"
  else
    sudo cp "${NGINX_CONF_PATH}/${GREEN_CONF}" "${NGINX_CONF_PATH}/${DEFAULT_CONF}"
  fi

  echo "Reloading NGINX configuration..."
  sudo nginx -s reload
}

# 이전 컨테이너 종료 함수
down_old_container() {
  if [ "$OLD" != "none" ]; then
    echo "Stopping old container: $OLD"
    docker compose -f docker-compose.yml stop "app-$OLD" || true
    docker compose -f docker-compose.yml rm -f "app-$OLD" || true
  fi
}

# 메인 실행 로직
main() {
  # 대상 컨테이너 결정
  determine_target

  local TARGET_SERVICE="app-$TARGET"

  # 컨테이너 충돌 방지: compose/비compose 둘 다 제거 시도
  echo "Removing any existing container with the name '$TARGET_SERVICE'..."
  docker compose -f docker-compose.yml rm -f "$TARGET_SERVICE" 2>/dev/null || true
  docker rm -f "$TARGET_SERVICE" 2>/dev/null || true

  # 새 컨테이너 실행
  echo "Starting new container: $TARGET_SERVICE..."
  docker compose -f docker-compose.yml up -d "$TARGET_SERVICE"

  # docker-ps 기반 헬스 체크 및 롤백
  if ! health_check; then
    echo "Health check failed. Initiating rollback..."

    # 실패한 컨테이너를 중지하고 제거
    echo "Removing failed container: '$TARGET_SERVICE'..."
    docker compose -f docker-compose.yml stop "$TARGET_SERVICE" || true
    docker compose -f docker-compose.yml rm -f "$TARGET_SERVICE" || true
    docker rm -f "$TARGET_SERVICE" 2>/dev/null || true

    echo "Rollback complete. The previous version remains active."
    exit 1
  fi

  # NGINX 설정 스위칭
  switch_nginx_conf

  # 이전 컨테이너 종료
  down_old_container

  echo "Deployment to $TARGET completed successfully!"

  echo "Cleaning up dangling Docker images..."
  docker image prune -f
}

# 스크립트 실행
main