#!/bin/bash
# KOINZ Block Notification Script
# Notifies KOINZ services of new Bitcoin blocks

# Configuration
KOINZ_SEEDNODE_HOST=${KOINZ_SEEDNODE_HOST:-"koinz-seednode"}
KOINZ_SEEDNODE_PORT=${KOINZ_SEEDNODE_PORT:-"5120"}
BLOCK_HASH=$1

# Logging
LOG_FILE="/var/log/koinz-blocknotify.log"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# Function to log messages
log_message() {
    echo "[$TIMESTAMP] $1" | tee -a "$LOG_FILE" 2>/dev/null || echo "[$TIMESTAMP] $1"
}

# Validate block hash
if [ -z "$BLOCK_HASH" ]; then
    log_message "ERROR: No block hash provided"
    exit 1
fi

# Validate block hash format (64 character hex string)
if ! echo "$BLOCK_HASH" | grep -qE '^[a-fA-F0-9]{64}$'; then
    log_message "ERROR: Invalid block hash format: $BLOCK_HASH"
    exit 1
fi

log_message "INFO: Notifying KOINZ services of new block: $BLOCK_HASH"

# Notify KOINZ seednode via TCP
notify_seednode() {
    local max_retries=3
    local retry_count=0
    
    while [ $retry_count -lt $max_retries ]; do
        if echo "$BLOCK_HASH" | nc -w 5 "$KOINZ_SEEDNODE_HOST" "$KOINZ_SEEDNODE_PORT" 2>/dev/null; then
            log_message "INFO: Successfully notified seednode at ${KOINZ_SEEDNODE_HOST}:${KOINZ_SEEDNODE_PORT}"
            return 0
        else
            retry_count=$((retry_count + 1))
            log_message "WARNING: Failed to notify seednode (attempt $retry_count/$max_retries)"
            sleep 1
        fi
    done
    
    log_message "ERROR: Failed to notify seednode after $max_retries attempts"
    return 1
}

# Notify other KOINZ services if configured
notify_additional_services() {
    # API service notification (if enabled)
    if [ -n "$KOINZ_API_HOST" ] && [ -n "$KOINZ_API_PORT" ]; then
        if echo "$BLOCK_HASH" | nc -w 5 "$KOINZ_API_HOST" "$KOINZ_API_PORT" 2>/dev/null; then
            log_message "INFO: Successfully notified API service at ${KOINZ_API_HOST}:${KOINZ_API_PORT}"
        else
            log_message "WARNING: Failed to notify API service at ${KOINZ_API_HOST}:${KOINZ_API_PORT}"
        fi
    fi
    
    # Statistics service notification (if enabled)
    if [ -n "$KOINZ_STATS_HOST" ] && [ -n "$KOINZ_STATS_PORT" ]; then
        if echo "$BLOCK_HASH" | nc -w 5 "$KOINZ_STATS_HOST" "$KOINZ_STATS_PORT" 2>/dev/null; then
            log_message "INFO: Successfully notified stats service at ${KOINZ_STATS_HOST}:${KOINZ_STATS_PORT}"
        else
            log_message "WARNING: Failed to notify stats service at ${KOINZ_STATS_HOST}:${KOINZ_STATS_PORT}"
        fi
    fi
}

# Main execution
main() {
    log_message "INFO: Processing block notification for hash: $BLOCK_HASH"
    
    # Primary notification to seednode
    if notify_seednode; then
        # Notify additional services
        notify_additional_services
        log_message "INFO: Block notification processing completed successfully"
        exit 0
    else
        log_message "ERROR: Critical failure - seednode notification failed"
        exit 1
    fi
}

# Execute main function
main "$@"