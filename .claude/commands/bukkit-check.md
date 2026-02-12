---
description: Check for common Bukkit plugin anti-patterns. Scans for main thread violations, printStackTrace usage, missing cleanup, and other issues.
---

# Bukkit Check

Scan the codebase for common Bukkit plugin anti-patterns and potential issues.

## What This Command Does

1. **Scan source code** for known anti-patterns
2. **Report findings** grouped by severity
3. **Suggest fixes** for each issue found

## Checks Performed

### CRITICAL - Must Fix

| Check | Pattern | Why |
|-------|---------|-----|
| Main thread violation | Bukkit API calls in async tasks | Server crash, undefined behavior |
| printStackTrace() usage | `e.printStackTrace()` | Use plugin logger instead |
| System.out/err usage | `System.out.println()` | Use plugin logger instead |
| Hardcoded credentials | Passwords, API keys in source | Security vulnerability |

### WARNING - Should Fix

| Check | Pattern | Why |
|-------|---------|-----|
| Missing onDisable cleanup | Tasks/listeners not cancelled | Memory leaks on reload |
| HashMap in multi-thread | `new HashMap<>()` in shared context | Use ConcurrentHashMap |
| Blocking main thread | Database/HTTP calls on main thread | Server lag/freeze |
| Empty catch blocks | `catch (Exception e) { }` | Silent failures |
| Raw types | `List` instead of `List<String>` | Type safety |

### INFO - Consider Fixing

| Check | Pattern | Why |
|-------|---------|-----|
| Missing @Override | Override without annotation | Compile-time safety |
| Mutable public fields | Public non-final fields | Encapsulation |
| Deprecated API usage | Using deprecated Bukkit methods | Future compatibility |

## Usage

```
/bukkit-check                    # Scan all modules
/bukkit-check {YourModule}       # Scan specific module
/bukkit-check --fix              # Auto-fix simple issues (printStackTrace -> logger)
```

## Scan Commands

```bash
# printStackTrace usage
grep -rn "\.printStackTrace()" --include="*.java" src/

# System.out usage
grep -rn "System\.\(out\|err\)" --include="*.java" src/

# Empty catch blocks
grep -rn "catch.*{" --include="*.java" -A1 src/ | grep -B1 "^.*}$"

# HashMap in potentially shared context
grep -rn "new HashMap<>" --include="*.java" src/

# Missing @Override on common methods
grep -rn "public void onEnable\|public void onDisable\|public void onReload" --include="*.java" src/
```

## Output Format

```
BUKKIT CHECK REPORT
===================

CRITICAL (2 issues)
  {YourModule}/src/.../{your/package}/SomeClass.java:45
    printStackTrace() - Use getPlugin().getLogger().severe() instead

  {YourModule}/src/.../{your/package}/AsyncTask.java:23
    Bukkit.getPlayer() called from async thread - Schedule to main thread

WARNING (3 issues)
  {YourModule}/src/.../{your/package}/MyModule.java
    No task cancellation in onDisable()

  {YourModule}/src/.../{your/package}/MenuManager.java:12
    HashMap used for player data - Consider ConcurrentHashMap

  {YourModule}/src/.../{your/package}/DatabaseModule.java:67
    Empty catch block - Log the exception

INFO (1 issue)
  {ApiModule}/src/.../{your/package}/ColorUtils.java:30
    Using deprecated ChatColor - Consider Adventure Component API

SUMMARY: 2 CRITICAL, 3 WARNING, 1 INFO
```

## Auto-Fix Support

The `--fix` flag can automatically fix these patterns:

| Pattern | Auto-Fix |
|---------|----------|
| `e.printStackTrace()` | Replace with `Bukkit.getLogger().severe(e.getMessage())` |
| `System.out.println(...)` | Replace with `Bukkit.getLogger().info(...)` |
| `System.err.println(...)` | Replace with `Bukkit.getLogger().warning(...)` |

Other issues require manual review and are reported only.

## Related

- See `error-handling.md` for logging guidelines
- See `async-concurrency.md` for thread safety rules
- See `security.md` for credential checks
