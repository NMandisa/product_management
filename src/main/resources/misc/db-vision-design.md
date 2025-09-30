Since I don't have direct access to your database, I'll provide you with the expected SQL structure and sample queries that should work based on the Hibernate entities we designed. Here's what you should see:

## Expected Table Structure

```sql
-- Show all tables
SHOW TABLES;

-- Expected output:
+-------------------------------+
| Tables_in_your_database       |
+-------------------------------+
| accessibility_settings        |
| additional_rule_flag          |
| additional_rule_threshold     |
| additional_rules              |
| allowed_currency_cross        |
| compliance_engine             |
| compliance_flag               |
| compliance_region             |
| currency                      |
| currency_formatting           |
| currency_fx_rules             |
| currency_placement_config     |
| currency_placement_exception  |
| date_format_config            |
| fx_backup_sources             |
| fx_configuration              |
| language                      |
| language_region               |
| localization_config           |
| localization_engine           |
| number_format_config          |
| regional_fallback_language    |
| regional_settings             |
| required_license              |
| supported_calendar            |
| supported_number_system       |
| time_format_config            |
+-------------------------------+
```

## Sample Queries with Expected Output

### 1. Compliance Regions
```sql
SELECT * FROM compliance_region;

-- Expected output (sample):
+-------------+--------------+---------+--------------------+-------------------+-------------+
| region_code | tax_authority | vat_rate | withholding_tax_rate | reporting_threshold | config_id   |
+-------------+--------------+---------+--------------------+-------------------+-------------+
| southAfrica | SARS         |    0.15 |               0.15  |             50000 | NULL        |
| nigeria     | FIRS         |   0.075 |                0.10 |             10000 | NULL        |
| kenya       | KRA          |    0.16 |               0.05  |            500000 | NULL        |
| ghana       | GRA          |   0.125 |               0.075 |             10000 | NULL        |
| global      | FATCA        |       0 |                0.30 |             10000 | NULL        |
+-------------+--------------+---------+--------------------+-------------------+-------------+
```

### 2. Languages
```sql
SELECT * FROM language;

-- Expected output (sample):
+-------------+-----------+-------------+------+------------+
| language_code | name     | native_name | rtl  | is_default |
+-------------+-----------+-------------+------+------------+
| en          | English   | English     | false| true       |
| zu          | Zulu      | isiZulu     | false| false      |
| af          | Afrikaans | Afrikaans   | false| false      |
| ar          | Arabic    | العربية     | true | false      |
| fr          | French    | Français    | false| false      |
| sw          | Swahili   | Kiswahili   | false| false      |
+-------------+-----------+-------------+------+------------+
```

### 3. Language Regions
```sql
SELECT * FROM language_region;

-- Expected output (sample):
+----+-------------+-------------+
| id | language_code | region_code |
+----+-------------+-------------+
|  1 | zu          | ZA          |
|  2 | af          | ZA          |
|  3 | fr          | SN          |
|  4 | fr          | CI          |
|  5 | sw          | KE          |
|  6 | sw          | TZ          |
|  7 | sw          | UG          |
|  8 | ar          | EG          |
+----+-------------+-------------+
```

### 4. Localization Config
```sql
SELECT * FROM localization_config;

-- Expected output (sample):
+----+------------+------------------------+-----------------------+---------------------+-----------------------+
| id | rtl_support | locale_aware_formatting | cultural_adaptations_id | accessibility_id | localization_engine_id |
+----+------------+------------------------+-----------------------+---------------------+-----------------------+
|  1 | true       | true                   |                     1 |                  1 |                     1 |
+----+------------+------------------------+-----------------------+---------------------+-----------------------+
```

### 5. Regional Settings
```sql
SELECT * FROM regional_settings;

-- Expected output:
+----+-----------------+-----------------+-------------+-------------+-------------------+
| id | region_group    | default_language | date_format | time_format | first_day_of_week |
+----+-----------------+-----------------+-------------+-------------+-------------------+
|  1 | southernAfrica  | en               | yyyy/MM/dd  | HH:mm       |                 0 |
|  2 | westAfrica      | en               | dd/MM/yyyy  | hh:mm a     |                 1 |
|  3 | eastAfrica      | sw               | dd-MM-yyyy  | HH:mm       |                 0 |
|  4 | northAfrica     | ar               | dd/MM/yyyy  | HH:mm       |                 6 |
+----+-----------------+-----------------+-------------+-------------+-------------------+
```

### 6. Regional Fallback Languages
```sql
SELECT * FROM regional_fallback_language;

-- Expected output:
+----+-------------+---------------+-----------------------+
| id | language_code | fallback_order | regional_settings_id |
+----+-------------+---------------+-----------------------+
|  1 | zu          |             1 |                     1 |
|  2 | af          |             2 |                     1 |
|  3 | fr          |             1 |                     2 |
|  4 | ha          |             2 |                     2 |
|  5 | yo          |             3 |                     2 |
|  6 | en          |             1 |                     3 |
|  7 | ar          |             2 |                     3 |
|  8 | fr          |             1 |                     4 |
|  9 | en          |             2 |                     4 |
+----+-------------+---------------+-----------------------+
```

### 7. Supported Calendars
```sql
SELECT * FROM supported_calendar;

-- Expected output:
+----+--------------+-----------------------++
| id | calendar_name | cultural_adaptations_id |
+----+--------------+-----------------------+
|  1 | gregorian    |                     1 |
|  2 | islamic      |                     1 |
|  3 | ethiopian    |                     1 |
+----+--------------+-----------------------+
```

### 8. Supported Number Systems
```sql
SELECT * FROM supported_number_system;

-- Expected output:
+----+---------------+-----------------------+
| id | number_system | cultural_adaptations_id |
+----+---------------+-----------------------+
|  1 | latin         |                     1 |
|  2 | arabic        |                     1 |
|  3 | devanagari    |                     1 |
+----+---------------+-----------------------+
```

### 9. Time Format Config
```sql
SELECT * FROM time_format_config;

-- Expected output:
+----+-------------+-----------+-----------------------+
| id | region_code | time_format | cultural_adaptations_id |
+----+-------------+-----------+-----------------------+
|  1 | ZA          | HH:mm     |                     1 |
|  2 | NG          | hh:mm a   |                     1 |
|  3 | KE          | HH:mm     |                     1 |
|  4 | NULL        | HH:mm     |                     1 |
+----+-------------+-----------+-----------------------+
```

### 10. Accessibility Settings
```sql
SELECT * FROM accessibility_settings;

-- Expected output:
+----+--------------+-----------+--------------+---------------+
| id | high_contrast | large_text | screen_reader | voice_commands |
+----+--------------+-----------+--------------+---------------+
|  1 | true         | true      | true         | false         |
+----+--------------+-----------+--------------+---------------+
```

## Troubleshooting Queries

If you're not seeing the expected data, run these diagnostic queries:

```sql
-- Check if tables exist
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE();

-- Check if data initializer ran
SELECT COUNT(*) as language_count FROM language;
SELECT COUNT(*) as compliance_region_count FROM compliance_region;

-- Check for any errors in the application logs during startup
```

## If Tables Are Empty

If the tables exist but are empty, the data initializer might not have run. Check:

1. **Application Properties**: Ensure `app.initialize.data=true`
2. **Hibernate DDL**: Check `spring.jpa.hibernate.ddl-auto` setting
3. **Application Logs**: Look for data initialization messages
4. **Database Permissions**: Ensure the application user has insert permissions


| **Category**          | **Field Name**                                    | **Type**          | **JPA Mapping / Notes**                                  |
|:----------------------|:--------------------------------------------------|:------------------|:---------------------------------------------------------|
| **Primary Key**       | `regionCode`                                      | `RegionCode`      | `@Id @Enumerated(EnumType.STRING)`                       |
| **Tax Information**   | `taxAuthority`                                    | `String`          | `@Column(nullable = false)`                              |
|                       | `vatRate`, `withholdingTaxRate`, `importDutyRate` | `BigDecimal`      | `@Column(precision=5, scale=2)`                          |
|                       | `reportingThreshold`                              | `BigDecimal`      | `@Column(precision=15, scale=2)`                         |
| **SA Compliance**     | `sarsBranchCode`                                  | `String`          | `@Column(length = 10)`                                   |
|                       | `cpaEnforced`, `bbrEnforced`                      | `Boolean`         | `@Column(nullable = false)`                              |
| **Status & Auditing** | `complianceLevel`                                 | `ComplianceLevel` | `@Enumerated(EnumType.STRING) @Column(nullable = false)` |
|                       | `lastAuditDate`, `nextAuditDue`                   | `LocalDateTime`   |                                                          |
|                       | `complianceScore`                                 | `BigDecimal`      | `@Column(precision=5, scale=2)`                          |
| **Regional Rules**    | `requiresTaxInvoice`                              | `Boolean`         | `@Column(nullable = false)`                              |
|                       | `taxInvoiceFormat`                                | `String`          |                                                          |
|                       | `minimumWage`, `environmentalLevy`                | `BigDecimal`      | `@Column(precision=10, scale=2)`                         |

