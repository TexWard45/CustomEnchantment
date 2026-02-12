# Code Review

Comprehensive security and quality review of uncommitted changes:

1. Get changed files: `git diff --name-only HEAD`

2. For each changed file, check for:

**Security Issues (CRITICAL):**
- Hardcoded credentials, API keys, tokens
- SQL injection vulnerabilities
- Command injection risks
- Missing input validation
- Insecure deserialization

**Code Quality (HIGH):**
- Methods > 50 lines
- Classes > 800 lines
- Nesting depth > 4 levels
- Missing error handling
- `printStackTrace()` or `System.out.println()` usage
- TODO/FIXME comments
- Missing Javadoc for public APIs

**Best Practices (MEDIUM):**
- Thread safety issues (HashMap in shared context)
- Bukkit API calls from async threads
- Missing @Override annotations
- Raw types (List instead of List<String>)
- Missing tests for new code
- Unused imports

3. Generate report with:
   - Severity: CRITICAL, HIGH, MEDIUM, LOW
   - File location and line numbers
   - Issue description
   - Suggested fix

4. Block commit if CRITICAL or HIGH issues found

Never approve code with security vulnerabilities!
