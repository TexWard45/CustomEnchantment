# Security Guidelines

## Mandatory Security Checks

Before ANY commit:
- [ ] No hardcoded secrets (API keys, passwords, tokens)
- [ ] All user inputs validated
- [ ] SQL injection prevention (parameterized queries)
- [ ] Error messages don't leak sensitive data
- [ ] No sensitive data in config defaults (redis passwords, DB credentials)

## Secret Management

```java
// NEVER: Hardcoded secrets
private String redisPassword = "mySecretPass123";

// ALWAYS: Load from config file (not committed to git)
@Path("redis.password")
private String redisPassword = "";
```

## Security Response Protocol

If security issue found:
1. STOP immediately
2. Fix CRITICAL issues before continuing
3. Rotate any exposed secrets
4. Review entire codebase for similar issues
