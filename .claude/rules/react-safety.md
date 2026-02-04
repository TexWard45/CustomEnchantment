# React Safety Rules

## 1. Prevent Infinite Loops in useEffect

### The Problem
When `useEffect` calls a function that updates state, and that function is in the dependency array, it creates an infinite loop.

### Rules

**NEVER** include state-updating functions in useEffect dependencies if they're called inside the effect:

```typescript
// ❌ WRONG - Infinite loop
const recordData = useCallback(() => {
  setState(newData);
}, [dependencies]);

useEffect(() => {
  recordData();
}, [recordData]); // INFINITE LOOP!
```

**ALWAYS** use refs to track previous values and only update on actual changes:

```typescript
// ✅ CORRECT - Use refs to detect changes
const prevValueRef = useRef(value);

useEffect(() => {
  if (prevValueRef.current !== value) {
    setState(newData);
    prevValueRef.current = value;
  }
}, [value, setState]); // Safe - setState is stable
```

**OR** inline the logic and only depend on stable values:

```typescript
// ✅ CORRECT - Inline logic
useEffect(() => {
  setState((prev) => {
    // Update logic here
    return newState;
  });
}, [stableValue1, stableValue2]); // Only stable dependencies
```

### Checklist
- [ ] useEffect dependencies include only stable values or primitives
- [ ] No callbacks that update state in dependency array
- [ ] Use refs to track previous values when needed
- [ ] Test component doesn't cause browser freeze

---

## 2. Prevent XSS Vulnerabilities

### The Problem
User-generated content or data from external sources (localStorage, API) can contain malicious scripts.

### Rules

**NEVER** use user data directly in JSX or strings without sanitization:

```typescript
// ❌ WRONG - XSS vulnerable
const message = `Welcome ${user.name}`; // If name contains <script>...
const html = { __html: userData }; // DANGEROUS!
```

**ALWAYS** sanitize user-generated content:

```typescript
// ✅ CORRECT - Sanitize HTML entities
function sanitizeText(text: string): string {
  return text.replace(/[<>"'&]/g, (char) => {
    const entities: Record<string, string> = {
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#x27;',
      '&': '&amp;',
    };
    return entities[char] ?? char;
  });
}

const message = `Welcome ${sanitizeText(user.name)}`;
```

**OR** use a trusted sanitization library:

```typescript
// ✅ CORRECT - Use DOMPurify for HTML
import DOMPurify from 'dompurify';

const cleanHtml = DOMPurify.sanitize(dirtyHtml);
```

### Where to Sanitize
- User input from forms
- Data from localStorage
- API responses with user-generated content
- URL parameters
- Markdown/HTML content

### Checklist
- [ ] All user input is sanitized before display
- [ ] localStorage data is validated and sanitized
- [ ] No use of dangerouslySetInnerHTML without sanitization
- [ ] API responses are validated before rendering

---

## 3. Validate External Data

### The Problem
Data from localStorage, APIs, or URL params can be corrupted, malicious, or unexpected types.

### Rules

**NEVER** trust external data types:

```typescript
// ❌ WRONG - Unsafe type casting
const data = JSON.parse(localStorage.getItem('key')) as MyType;
```

**ALWAYS** validate data structure and types:

```typescript
// ✅ CORRECT - Validate before using
try {
  const item = window.localStorage.getItem(key);
  if (!item) return initialValue;

  const parsed = JSON.parse(item);

  // Type validation
  if (parsed == null || typeof parsed !== typeof initialValue) {
    console.warn(`Invalid data type for key "${key}"`);
    window.localStorage.removeItem(key);
    return initialValue;
  }

  return parsed as T;
} catch (error) {
  console.error(`Error reading key "${key}":`, error);
  window.localStorage.removeItem(key); // Clean up corrupted data
  return initialValue;
}
```

**BETTER** - Use Zod schemas for runtime validation:

```typescript
// ✅ BEST - Runtime type validation with Zod
import { z } from 'zod';

const UserSchema = z.object({
  id: z.string(),
  name: z.string(),
  age: z.number().int().positive(),
});

try {
  const parsed = JSON.parse(data);
  const validated = UserSchema.parse(parsed); // Throws if invalid
  return validated;
} catch (error) {
  console.error('Validation failed:', error);
  return null;
}
```

### Data Sources to Validate
1. **localStorage/sessionStorage**
   - Can be corrupted
   - Can be tampered with
   - Dates become strings

2. **API Responses**
   - Network errors
   - Unexpected structure
   - Type mismatches

3. **URL Parameters**
   - User can modify
   - Always strings
   - Can be malicious

4. **Form Input**
   - User-controlled
   - Can be malicious
   - Needs sanitization

### Checklist
- [ ] All external data has try-catch blocks
- [ ] Type validation before use
- [ ] Corrupted data is cleaned up (removed from storage)
- [ ] Fallback values provided for invalid data
- [ ] Consider using Zod for complex validation

---

## 4. No Console Statements in Production

### The Problem
Console statements in production leak information, slow performance, and look unprofessional.

### Rules

**NEVER** leave bare console statements:

```typescript
// ❌ WRONG - Visible in production
console.log('User data:', userData);
console.warn('Something happened');
console.error('Error:', error);
```

**ALWAYS** guard console statements:

```typescript
// ✅ CORRECT - Development only
if (process.env.NODE_ENV === 'development') {
  console.log('Debug info:', data);
}
```

**BETTER** - Use a logging utility:

```typescript
// ✅ BEST - Centralized logging
const logger = {
  debug: (message: string, ...args: any[]) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(`[DEBUG] ${message}`, ...args);
    }
  },
  error: (message: string, error: unknown) => {
    if (process.env.NODE_ENV === 'development') {
      console.error(`[ERROR] ${message}`, error);
    }
    // In production, send to error tracking service
    // errorTrackingService.log(message, error);
  },
};
```

**BEST** - Use proper error handling:

```typescript
// ✅ IDEAL - Return status instead of logging
const switchTo = (mode: ThinkingMode): { success: boolean; reason?: string } => {
  if (isProcessing) {
    return { success: false, reason: 'Cannot switch while processing' };
  }

  setMode(mode);
  return { success: true };
};
```

### Exceptions
Only acceptable console use:
- Build scripts (Node.js context)
- Development-only debugging (with NODE_ENV guard)
- Error boundaries (with guard and error tracking)

### Checklist
- [ ] No bare console.log/warn/error statements
- [ ] All console statements guarded by NODE_ENV check
- [ ] Consider returning status objects instead
- [ ] Production builds have zero console output

---

## React Safety Checklist

Before committing any React code:
- [ ] No useEffect infinite loops (test in browser)
- [ ] All user data is sanitized
- [ ] External data (localStorage, API) is validated
- [ ] No unguarded console statements
- [ ] Component doesn't freeze browser
- [ ] No XSS vulnerabilities
- [ ] Error boundaries handle crashes
- [ ] Cleanup functions for all effects

---

## Common Pitfalls

### 1. useEffect with Objects/Arrays
```typescript
// ❌ WRONG - Runs every render (new object each time)
useEffect(() => {
  doSomething();
}, [{ value: x }]); // New object = new reference

// ✅ CORRECT - Depend on primitives
useEffect(() => {
  doSomething();
}, [x]); // Primitive value
```

### 2. Stale Closures
```typescript
// ❌ WRONG - Stale value
useEffect(() => {
  setTimeout(() => {
    console.log(count); // Stale!
  }, 1000);
}, []);

// ✅ CORRECT - Include in dependencies
useEffect(() => {
  setTimeout(() => {
    console.log(count); // Fresh value
  }, 1000);
}, [count]);
```

### 3. Missing Cleanup
```typescript
// ❌ WRONG - Memory leak
useEffect(() => {
  window.addEventListener('scroll', handleScroll);
}, []);

// ✅ CORRECT - Cleanup
useEffect(() => {
  window.addEventListener('scroll', handleScroll);
  return () => window.removeEventListener('scroll', handleScroll);
}, []);
```

---

## Testing for React Safety

Required tests:
1. **useEffect loops:** Render component, wait 100ms, check state updated only once
2. **XSS:** Pass `<script>alert('xss')</script>` as input, verify it's escaped
3. **Invalid data:** Pass corrupted JSON to localStorage hook, verify it recovers
4. **Console output:** Check production build has zero console statements

Example test:
```typescript
it('should not create infinite loop', async () => {
  const { rerender } = render(<MyComponent />);

  // Wait for any async updates
  await waitFor(() => {}, { timeout: 100 });

  // Component should still be responsive
  expect(screen.getByText('Content')).toBeInTheDocument();

  // No excessive re-renders (add render counter in component)
  expect(renderCount).toBeLessThan(5);
});
```
