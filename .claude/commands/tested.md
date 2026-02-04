# Tested Command

Mark issues as human-tested or list untested issues.

## Usage

```
/tested                    # Mark current branch's issue as tested
/tested #15                # Mark specific issue as tested
/tested #15 "Works great"  # Mark with test notes
/tested --list             # Show all untested issues
/tested -l                 # Same as --list
/tested --pending          # Same as --list
```

## What This Command Does

### 1. Mark Issue as Tested

When marking an issue as tested:

**GitHub Actions:**
```bash
# Add "tested" label to issue
gh issue edit <number> --add-label "tested"

# Add comment with test confirmation
gh issue comment <number> --body "$(cat <<'EOF'
## Testing Confirmation

Tested by human on YYYY-MM-DD

<notes if provided>

---
*Added via `/tested` command*
EOF
)"
```

**Local Documentation:**
Update or create `.claude/docs/issues/<number>-<slug>.md` with Testing section:

```markdown
## Testing

- [x] Tested by human
- Date: YYYY-MM-DD
- Tester: Human (manual)
- Notes: <notes if provided>
```

### 2. List Untested Issues

When using `--list`, `-l`, or `--pending`:

**Actions:**
1. Fetch all CLOSED issues that have been merged recently
2. Filter issues WITHOUT "tested" label
3. Cross-check with `.claude/docs/issues/` for testing status
4. Display formatted list

**Output Format:**
```
UNTESTED ISSUES
===============================================
#37  feat: English Practice Support        [M] CLOSED
#39  feat: Knowledge Graph visualization   [L] CLOSED
#64  fix: Capture layout centering         [S] CLOSED

Total: 3 issues need testing

Legend: [S]=Small [M]=Medium [L]=Large [XL]=Extra Large

To mark as tested:
  /tested #37
  /tested #37 "Verified on Chrome and Firefox"
```

## Auto-Detection

When `/tested` is called without an issue number:

1. Check current git branch name
2. Extract issue number from branch (e.g., `37-feat-english-practice` -> #37)
3. If no branch match, prompt user for issue number

```
# Example auto-detection
Current branch: 37-feat-english-practice
Detected issue: #37

Marking #37 as tested...
```

## Workflow Integration

### After PR Merge

Typical workflow after merging a PR:

```
1. gh pr merge 76 --squash --delete-branch
2. # User tests the feature manually
3. /tested #37 "Verified rewrite feature works with all tones"
```

### Before Release

Check all features are tested:

```
/tested --list

# If any untested:
# Test each feature manually, then mark:
/tested #37
/tested #39 "Graph renders correctly"
```

## Label Management

The command expects a "tested" label to exist in the repository.

**Create label if missing:**
```bash
gh label create "tested" --color "0E8A16" --description "Manually tested by human"
```

## Examples

### Mark Current Branch's Issue
```
User: /tested

Claude:
Detected issue #37 from branch 37-feat-english-practice

Marking #37 as tested...
- Added label: tested
- Added comment: Testing confirmed
- Updated: .claude/docs/issues/37-english-practice.md

Issue #37 marked as tested.
```

### Mark with Notes
```
User: /tested #39 "Graph visualization works, tested pan/zoom/click"

Claude:
Marking #39 as tested...
- Added label: tested
- Added comment with notes
- Updated local spec file

Issue #39 marked as tested.
Notes: "Graph visualization works, tested pan/zoom/click"
```

### List Untested
```
User: /tested --list

Claude:
UNTESTED ISSUES
===============================================
#72  feat: Prioritize command             [M] OPEN
#67  fix: Space dropdown overflow         [S] CLOSED

Total: 2 issues need testing

To mark as tested:
  /tested #72
  /tested #67 "Dropdown no longer clips"
```

## Error Handling

### Issue Not Found
```
ERROR: Issue #999 not found
```

### Already Tested
```
INFO: Issue #37 already has "tested" label
Add another test note anyway? (yes/no)
```

### No Branch Detected
```
ERROR: Could not detect issue from branch 'main'
Please specify issue number: /tested #<number>
```

## Arguments

- `<issue-number>` - GitHub issue number (optional, auto-detected from branch)
- `<notes>` - Test notes in quotes (optional)
- `--list`, `-l`, `--pending` - List untested issues

## Related Commands

- `/resolve` - Complete issue resolution workflow
- `/code-review` - Review code before testing
- `/checkpoint` - Create checkpoints during work

## Implementation Notes

### Detecting Issue from Branch

```bash
# Get current branch
branch=$(git branch --show-current)

# Extract issue number (assumes format: <number>-<type>-<description>)
issue_number=$(echo "$branch" | grep -oE '^[0-9]+')
```

### Checking Existing Label

```bash
# Check if issue has "tested" label
labels=$(gh issue view <number> --json labels -q '.labels[].name')
if echo "$labels" | grep -q "tested"; then
  echo "Already tested"
fi
```

### Fetching Untested Issues

```bash
# Get closed issues without "tested" label
gh issue list --state closed --limit 50 --json number,title,labels \
  --jq '.[] | select(.labels | map(.name) | contains(["tested"]) | not)'
```
