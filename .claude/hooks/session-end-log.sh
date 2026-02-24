#!/bin/bash
# Session End Logger — logs session metadata for analysis
# Part of #70: Session Learning Hooks

LOG_DIR="$HOME/.claude/session-logs"
mkdir -p "$LOG_DIR"

DATE=$(date -Iseconds 2>/dev/null || date)
CWD=$(pwd)

cat >> "$LOG_DIR/sessions.log" << EOF
---
date: $DATE
cwd: $CWD
EOF
