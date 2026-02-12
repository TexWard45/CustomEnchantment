# Configuration Rules

## Annotation-Based Config

All config classes use `@Configuration` and `@Path`:

```java
@Getter
@Configuration
public class MainConfig {
    @Path("feature.enabled")
    private boolean enabled = true;

    @Path("feature.max-count")
    private int maxCount = 10;

    @Path("feature.message")
    private String message = "Default message";
}
```

## Field Naming

| YAML Path | Java Field |
|-----------|------------|
| `feature.enabled` | `boolean enabled` |
| `feature.max-count` | `int maxCount` |
| `redis.host` | `String redisHost` |
| `database` | `DatabaseConfig databaseConfig` |

- YAML uses kebab-case: `max-count`
- Java uses camelCase: `maxCount`
- Nested objects use the config class type: `DatabaseConfig`

## Default Values

Always provide sensible defaults so the plugin works without config:

```java
// GOOD: Has defaults
@Path("redis.host")
private String redisHost = "localhost";

@Path("redis.port")
private int redisPort = 6379;

@Path("redis.password")
private String redisPassword = "";

// BAD: No defaults — NPE if config missing
@Path("redis.host")
private String redisHost;
```

## Nested Config Objects

Use `@Configuration` on nested classes too:

```java
@Getter
@Configuration
public class ChannelData {
    @Path
    private String name;
}

// Parent config
@Getter
@Configuration
public class MainConfig {
    @Path("redis.host-channel")
    private ChannelData hostChannelData = new ChannelData();
}
```

## Map/List Fields

For maps, specify value type with `@ValueType`:

```java
@Path("redis.sub-channel")
@ValueType(ChannelData.class)
private Map<String, ChannelData> subChannelMap = new LinkedHashMap<>();

@Path("debug.packet-blacklist")
private List<String> packetBlacklist = new ArrayList<>();
```

Always initialize collections to empty (not null).

## Loading Config

Use `ConfigUtils.setupResource()` in `onReload()`:

```java
@Override
public void onReload() {
    AdvancedFileConfiguration config =
        ConfigUtils.setupResource(getPlugin(), "/config.yml", getConfigFile());
    this.mainConfig = config.get(MainConfig.class);
}
```

## Sensitive Values

Never commit sensitive defaults. Use empty strings:

```java
// GOOD
@Path("redis.password")
private String redisPassword = "";

@Path("database.password")
private String password = "";

// BAD — password in source code
@Path("redis.password")
private String redisPassword = "mySecretPass";
```
