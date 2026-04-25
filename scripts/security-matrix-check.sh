#!/usr/bin/env bash

set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
RESPONSE_FILE="${RESPONSE_FILE:-${TMPDIR:-/tmp}/security_matrix_response.json}"
CURL_ERROR_FILE="${CURL_ERROR_FILE:-${TMPDIR:-/tmp}/security_matrix_curl_error.txt}"

USER_NAME="${USER_NAME:-user}"
USER_PASS="${USER_PASS:-user123}"

ADMIN_NAME="${ADMIN_NAME:-admin}"
ADMIN_PASS="${ADMIN_PASS:-admin123}"

# Adjust these if your DTO field names differ.
PROJECT_BODY="${PROJECT_BODY:-{\"title\":\"Security Matrix Project\",\"description\":\"Created by security matrix script\"}}"
PROJECT_UPDATE_BODY="${PROJECT_UPDATE_BODY:-{\"title\":\"Security Matrix Project Updated\",\"description\":\"Updated by security matrix script\",\"completed\":false}}"
TASK_BODY="${TASK_BODY:-{\"title\":\"Security Matrix Task\",\"description\":\"Created by security matrix script\",\"priority\":\"MEDIUM\",\"dueDate\":\"2026-05-01T00:00:00Z\"}}"
TASK_UPDATE_BODY="${TASK_UPDATE_BODY:-{\"title\":\"Security Matrix Task Updated\",\"description\":\"Updated by security matrix script\",\"priority\":\"HIGH\",\"dueDate\":\"2026-05-02T00:00:00Z\"}}"
STATUS_BODY="${STATUS_BODY:-{\"status\":\"IN_PROGRESS\"}}"

# Set this to 401 if your project reads are protected.
EXPECT_ANON_PROJECT_READ="${EXPECT_ANON_PROJECT_READ:-200}"

pass_count=0
fail_count=0

green() { printf "\033[32m%s\033[0m\n" "$1"; }
red() { printf "\033[31m%s\033[0m\n" "$1"; }
yellow() { printf "\033[33m%s\033[0m\n" "$1"; }

status_code() {
  local method="$1"
  local url="$2"
  local auth="${3:-}"
  local body="${4:-}"

  rm -f "$RESPONSE_FILE" "$CURL_ERROR_FILE"

  local curl_args=(
    -sS
    -o "$RESPONSE_FILE"
    -w "%{http_code}"
    -X "$method"
  )

  if [[ -n "$auth" ]]; then
    curl_args+=(-u "$auth")
  fi

  if [[ -n "$body" ]]; then
    curl_args+=(-H "Content-Type: application/json" -d "$body")
  fi

  local code
  code=$(curl "${curl_args[@]}" "$url" 2>"$CURL_ERROR_FILE")
  local curl_exit=$?

  if [[ "$curl_exit" -ne 0 ]]; then
    {
      echo "curl failed with exit code $curl_exit"
      cat "$CURL_ERROR_FILE"
    } > "$RESPONSE_FILE"
    echo "curl_error"
    return
  fi

  echo "$code"
}

expect() {
  local label="$1"
  local actual="$2"
  local expected="$3"

  if [[ "$actual" == "$expected" ]]; then
    green "PASS  $label -> $actual"
    pass_count=$((pass_count + 1))
  else
    red "FAIL  $label -> expected $expected, got $actual"
    echo "Response body:"
    cat "$RESPONSE_FILE"
    echo
    fail_count=$((fail_count + 1))
  fi
}

expect_any_2xx() {
  local label="$1"
  local actual="$2"

  if [[ "$actual" =~ ^2[0-9][0-9]$ ]]; then
    green "PASS  $label -> $actual"
    pass_count=$((pass_count + 1))
  else
    red "FAIL  $label -> expected 2xx, got $actual"
    echo "Response body:"
    cat "$RESPONSE_FILE"
    echo
    fail_count=$((fail_count + 1))
  fi
}

extract_id() {
  if ! command -v jq >/dev/null 2>&1; then
    echo ""
    return
  fi

  jq -r '.id // .projectId // .taskId // empty' "$RESPONSE_FILE"
}

create_project_as_admin() {
  local code
  code=$(status_code "POST" "$BASE_URL/api/projects" "$ADMIN_NAME:$ADMIN_PASS" "$PROJECT_BODY")

  if [[ ! "$code" =~ ^2[0-9][0-9]$ ]]; then
    red "Could not create setup project as ADMIN. Got $code."
    cat "$RESPONSE_FILE"
    echo
    exit 1
  fi

  local id
  id=$(extract_id)

  if [[ -z "$id" ]]; then
    red "Could not extract project id from create-project response."
    echo "Install jq or adjust extract_id(). Response body:"
    cat "$RESPONSE_FILE"
    echo
    exit 1
  fi

  echo "$id"
}

create_task_as_admin() {
  local project_id="$1"

  local code
  code=$(status_code "POST" "$BASE_URL/api/projects/$project_id/tasks" "$ADMIN_NAME:$ADMIN_PASS" "$TASK_BODY")

  if [[ ! "$code" =~ ^2[0-9][0-9]$ ]]; then
    red "Could not create setup task as ADMIN. Got $code."
    cat "$RESPONSE_FILE"
    echo
    exit 1
  fi

  local id
  id=$(extract_id)

  if [[ -z "$id" ]]; then
    red "Could not extract task id from create-task response."
    echo "Install jq or adjust extract_id(). Response body:"
    cat "$RESPONSE_FILE"
    echo
    exit 1
  fi

  echo "$id"
}

echo
yellow "Checking TaskPlanner security matrix against $BASE_URL"
echo

echo "Creating setup data as ADMIN..."
PROJECT_ID=$(create_project_as_admin)
PROJECT_ID_FOR_USER_UPDATE=$(create_project_as_admin)
PROJECT_ID_FOR_ADMIN_UPDATE=$(create_project_as_admin)
PROJECT_ID_FOR_DELETE=$(create_project_as_admin)
TASK_ID_FOR_USER_DELETE=$(create_task_as_admin "$PROJECT_ID")
TASK_ID_FOR_ADMIN_DELETE=$(create_task_as_admin "$PROJECT_ID")
TASK_ID_FOR_USER_PUT=$(create_task_as_admin "$PROJECT_ID")
TASK_ID_FOR_ADMIN_PUT=$(create_task_as_admin "$PROJECT_ID")
TASK_ID_FOR_USER_PATCH=$(create_task_as_admin "$PROJECT_ID")
TASK_ID_FOR_ADMIN_PATCH=$(create_task_as_admin "$PROJECT_ID")

echo "Setup project id: $PROJECT_ID"
echo

# -------------------------------------------------------------------
# Matrix checks
# -------------------------------------------------------------------

code=$(status_code "GET" "$BASE_URL/actuator/health")
expect "anonymous GET /actuator/health" "$code" "200"

code=$(status_code "GET" "$BASE_URL/actuator/health" "$USER_NAME:$USER_PASS")
expect "USER GET /actuator/health" "$code" "200"

code=$(status_code "GET" "$BASE_URL/actuator/health" "$ADMIN_NAME:$ADMIN_PASS")
expect "ADMIN GET /actuator/health" "$code" "200"

echo

code=$(status_code "GET" "$BASE_URL/api/projects")
expect "anonymous GET /api/projects" "$code" "$EXPECT_ANON_PROJECT_READ"

code=$(status_code "GET" "$BASE_URL/api/projects" "$USER_NAME:$USER_PASS")
expect "USER GET /api/projects" "$code" "200"

code=$(status_code "GET" "$BASE_URL/api/projects" "$ADMIN_NAME:$ADMIN_PASS")
expect "ADMIN GET /api/projects" "$code" "200"

echo

code=$(status_code "POST" "$BASE_URL/api/projects" "" "$PROJECT_BODY")
expect "anonymous POST /api/projects" "$code" "401"

code=$(status_code "POST" "$BASE_URL/api/projects" "$USER_NAME:$USER_PASS" "$PROJECT_BODY")
expect "USER POST /api/projects" "$code" "403"

code=$(status_code "POST" "$BASE_URL/api/projects" "$ADMIN_NAME:$ADMIN_PASS" "$PROJECT_BODY")
expect_any_2xx "ADMIN POST /api/projects" "$code"

echo

code=$(status_code "PUT" "$BASE_URL/api/projects/$PROJECT_ID_FOR_USER_UPDATE" "" "$PROJECT_UPDATE_BODY")
expect "anonymous PUT /api/projects/{id}" "$code" "401"

code=$(status_code "PUT" "$BASE_URL/api/projects/$PROJECT_ID_FOR_USER_UPDATE" "$USER_NAME:$USER_PASS" "$PROJECT_UPDATE_BODY")
expect_any_2xx "USER PUT /api/projects/{id}" "$code"

code=$(status_code "PUT" "$BASE_URL/api/projects/$PROJECT_ID_FOR_ADMIN_UPDATE" "$ADMIN_NAME:$ADMIN_PASS" "$PROJECT_UPDATE_BODY")
expect_any_2xx "ADMIN PUT /api/projects/{id}" "$code"

echo

code=$(status_code "DELETE" "$BASE_URL/api/projects/$PROJECT_ID_FOR_DELETE")
expect "anonymous DELETE /api/projects/{id}" "$code" "401"

code=$(status_code "DELETE" "$BASE_URL/api/projects/$PROJECT_ID_FOR_DELETE" "$USER_NAME:$USER_PASS")
expect "USER DELETE /api/projects/{id}" "$code" "403"

code=$(status_code "DELETE" "$BASE_URL/api/projects/$PROJECT_ID_FOR_DELETE" "$ADMIN_NAME:$ADMIN_PASS")
expect "ADMIN DELETE /api/projects/{id}" "$code" "204"

echo

code=$(status_code "POST" "$BASE_URL/api/projects/$PROJECT_ID/tasks" "" "$TASK_BODY")
expect "anonymous POST /api/projects/{id}/tasks" "$code" "401"

code=$(status_code "POST" "$BASE_URL/api/projects/$PROJECT_ID/tasks" "$USER_NAME:$USER_PASS" "$TASK_BODY")
expect_any_2xx "USER POST /api/projects/{id}/tasks" "$code"

code=$(status_code "POST" "$BASE_URL/api/projects/$PROJECT_ID/tasks" "$ADMIN_NAME:$ADMIN_PASS" "$TASK_BODY")
expect_any_2xx "ADMIN POST /api/projects/{id}/tasks" "$code"

echo

code=$(status_code "PUT" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_PUT" "" "$TASK_UPDATE_BODY")
expect "anonymous PUT /api/tasks/{id}" "$code" "401"

code=$(status_code "PUT" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_PUT" "$USER_NAME:$USER_PASS" "$TASK_UPDATE_BODY")
expect_any_2xx "USER PUT /api/tasks/{id}" "$code"

code=$(status_code "PUT" "$BASE_URL/api/tasks/$TASK_ID_FOR_ADMIN_PUT" "$ADMIN_NAME:$ADMIN_PASS" "$TASK_UPDATE_BODY")
expect_any_2xx "ADMIN PUT /api/tasks/{id}" "$code"

echo

code=$(status_code "PATCH" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_PATCH/status" "" "$STATUS_BODY")
expect "anonymous PATCH /api/tasks/{id}/status" "$code" "401"

code=$(status_code "PATCH" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_PATCH/status" "$USER_NAME:$USER_PASS" "$STATUS_BODY")
expect_any_2xx "USER PATCH /api/tasks/{id}/status" "$code"

code=$(status_code "PATCH" "$BASE_URL/api/tasks/$TASK_ID_FOR_ADMIN_PATCH/status" "$ADMIN_NAME:$ADMIN_PASS" "$STATUS_BODY")
expect_any_2xx "ADMIN PATCH /api/tasks/{id}/status" "$code"

echo

code=$(status_code "DELETE" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_DELETE")
expect "anonymous DELETE /api/tasks/{id}" "$code" "401"

code=$(status_code "DELETE" "$BASE_URL/api/tasks/$TASK_ID_FOR_USER_DELETE" "$USER_NAME:$USER_PASS")
expect "USER DELETE /api/tasks/{id}" "$code" "204"

code=$(status_code "DELETE" "$BASE_URL/api/tasks/$TASK_ID_FOR_ADMIN_DELETE" "$ADMIN_NAME:$ADMIN_PASS")
expect "ADMIN DELETE /api/tasks/{id}" "$code" "204"

echo
yellow "Summary"
echo "Passed: $pass_count"
echo "Failed: $fail_count"

if [[ "$fail_count" -eq 0 ]]; then
  green "Security matrix passed."
  exit 0
else
  red "Security matrix failed."
  exit 1
fi
