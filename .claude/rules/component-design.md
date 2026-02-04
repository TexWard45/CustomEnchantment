# Component Design Guidelines

## 1. Avoid Hardcoded Values

### The Problem
Hardcoded values (magic numbers, strings) make components inflexible and difficult to maintain.

### Bad Patterns

```tsx
// Hardcoded dimensions
<div className="w-[280px] h-[400px]" />

// Magic numbers in logic
if (luminance > 0.5) { ... }

// Hardcoded strings
<textarea placeholder="What's on your mind right now?" />

// Inline pixel values
<div style={{ minHeight: 150, maxHeight: 400 }} />
```

### Good Patterns

#### Option 1: Configurable Props with Defaults

```tsx
interface ComponentProps {
  /** Minimum height in pixels */
  minHeight?: number;
  /** Maximum height in pixels */
  maxHeight?: number;
}

function Component({
  minHeight = 150,
  maxHeight = 400,
}: ComponentProps) {
  // Use props with sensible defaults
}
```

#### Option 2: Centralized Config File

```tsx
// src/config/ui.config.ts
export const captureConfig = {
  textareaMinHeight: 150,
  textareaMaxHeight: 400,
  textareaPlaceholder: "What's on your mind right now?",
} as const;

// Component usage
import { captureConfig } from '~/config/ui.config';

function CaptureTextArea() {
  const { textareaMinHeight, textareaMaxHeight } = captureConfig;
  // ...
}
```

#### Option 3: Theme/Design Tokens

```css
/* globals.css */
:root {
  --capture-min-height: 150px;
  --capture-max-height: 400px;
}
```

```tsx
// Use CSS variables
<textarea style={{ minHeight: 'var(--capture-min-height)' }} />
```

### When to Use Each Pattern

| Pattern | Use When |
|---------|----------|
| **Props with defaults** | Component consumers might customize values |
| **Config file** | Values shared across multiple components |
| **Design tokens** | Consistent theming across the entire app |

---

## 2. Component Prop Interface Rules

### Every Prop Must Have Documentation

```tsx
interface ButtonProps {
  /** Button content */
  children: ReactNode;
  /** Visual style variant
   * @default 'primary'
   */
  variant?: 'primary' | 'secondary' | 'ghost';
  /** Whether the button is disabled
   * @default false
   */
  disabled?: boolean;
}
```

### Use Descriptive Prop Names

```tsx
// Bad
interface Props {
  h?: number; // What is h?
  t?: string; // What is t?
}

// Good
interface Props {
  minHeight?: number;
  placeholder?: string;
}
```

### Optional Props Should Have Defaults

```tsx
// Bad - no default
function Component({ size }: { size?: 'sm' | 'md' | 'lg' }) {
  // What if size is undefined?
}

// Good - explicit default
function Component({ size = 'md' }: { size?: 'sm' | 'md' | 'lg' }) {
  // size always has a value
}
```

---

## 3. Single Responsibility Principle

### Each Component Should Do One Thing

```tsx
// Bad - does too much
function CaptureWithResponseAndTopics() {
  // Handles capture, response display, AND topic management
}

// Good - split into focused components
function CaptureTextArea() { /* Just the textarea */ }
function CaptureResponse() { /* Just the AI response */ }
function TopicChip() { /* Just a single topic tag */ }
```

### Component Size Limits

| Metric | Limit | Reason |
|--------|-------|--------|
| Lines of code | < 200 | Easier to understand and test |
| Props | < 10 | Simpler API, fewer decisions |
| Nested conditionals | < 4 levels | Readable, maintainable |
| useEffect hooks | < 3 | Fewer side effects to track |

---

## 4. Import Shared Utilities

### Never Duplicate Utility Functions

```tsx
// BAD - duplicated in each file
function cn(...classes: (string | undefined | false)[]) {
  return classes.filter(Boolean).join(' ');
}

// GOOD - import from shared location
import { cn } from '~/lib/utils';
```

### Common Utilities to Import

| Utility | Location | Purpose |
|---------|----------|---------|
| `cn` | `~/lib/utils` | Class name merging (clsx + tailwind-merge) |
| `formatDate` | `~/lib/utils` | Date formatting |
| `sanitizeText` | `~/lib/sanitize` | XSS prevention |

---

## 5. Accessibility by Default

### Every Interactive Element Needs

```tsx
// ARIA label for screen readers
<button aria-label="Close dialog">×</button>

// Focus visible for keyboard users
<button className="focus-visible:ring-2 focus-visible:ring-primary">

// Semantic HTML
<nav aria-label="Main navigation">
<main aria-labelledby="page-title">
<aside aria-label="Related content">
```

### Minimum Touch Target

```css
/* All interactive elements: min 44x44px */
.button {
  min-height: 44px;
  min-width: 44px;
}
```

---

## 6. TypeScript Best Practices

### Export Prop Interfaces

```tsx
// Allow consumers to extend or use the type
export interface ButtonProps {
  // ...
}

export function Button(props: ButtonProps) {
  // ...
}
```

### Use `ComponentPropsWithoutRef` for Extension

```tsx
import type { ComponentPropsWithoutRef } from 'react';

// Extend native button props
interface ButtonProps extends ComponentPropsWithoutRef<'button'> {
  variant?: 'primary' | 'secondary';
}
```

### Avoid `any` Type

```tsx
// Bad
function handleClick(event: any) { ... }

// Good
function handleClick(event: React.MouseEvent<HTMLButtonElement>) { ... }
```

---

## 7. Component File Structure

### Recommended Order

```tsx
/**
 * Component description
 */

// 1. Imports
import type { ... } from 'react';
import { cn } from '~/lib/utils';

// 2. Types/Interfaces
interface ComponentProps {
  // ...
}

// 3. Constants (if needed)
const DEFAULT_VALUE = 100;

// 4. Helper functions (if needed)
function helperFunction() { ... }

// 5. Main component
export function Component(props: ComponentProps) {
  // ...
}

// 6. Sub-components (if any, consider separate file if >50 lines)
function SubComponent() { ... }
```

---

## 8. Checklist Before Creating a Component

- [ ] Does a similar component already exist?
- [ ] Can I extend an existing component instead?
- [ ] Are all hardcoded values extracted to props or config?
- [ ] Is the prop interface documented with JSDoc?
- [ ] Am I importing shared utilities (not duplicating)?
- [ ] Is the component under 200 lines?
- [ ] Does it have less than 10 props?
- [ ] Is it accessible (ARIA labels, keyboard nav, focus states)?
- [ ] Does it follow the file structure convention?
- [ ] Are TypeScript types strict (no `any`)?

---

## 9. Configuration File Reference

The project uses `src/config/ui.config.ts` for centralized UI configuration.

### Available Configs

| Config | Purpose |
|--------|---------|
| `layoutConfig` | Three-column layout dimensions |
| `captureConfig` | Capture mode component settings |
| `skeletonConfig` | Loading skeleton widths |
| `navigationConfig` | Context navigator spacing |
| `topicChipConfig` | Topic chip styling |
| `animationConfig` | Transition durations |
| `a11yConfig` | Accessibility settings |
| `typographyConfig` | Font size scale |
| `buttonConfig` | Button size presets |
| `spinnerConfig` | Loading spinner sizes |
| `timeFilterOptions` | Time filter dropdown options |

### Usage Example

```tsx
import { captureConfig, layoutConfig } from '~/config/ui.config';

function MyComponent() {
  return (
    <textarea
      style={{ minHeight: captureConfig.textareaMinHeight }}
      placeholder={captureConfig.textareaPlaceholder}
    />
  );
}
```

---

## Summary

| Rule | Action |
|------|--------|
| No hardcoded values | Use props with defaults OR config file |
| Document all props | JSDoc comments with `@default` |
| Single responsibility | < 200 lines, < 10 props |
| Import shared utilities | `cn` from `~/lib/utils` |
| Accessibility first | ARIA labels, focus states, semantic HTML |
| TypeScript strict | No `any`, export interfaces |
| Consistent structure | Follow file organization pattern |
