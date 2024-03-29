# Backtrace Java Release Notes

## Version 0.9.7 - 05.07.2023
- Remove 'com.google.guava:guava' library

## Version 0.9.6 - 24.05.2023
- Update dependencies (GSON and slf4j-api)

## Version 0.9.5 - 03.08.2022
- Fix null attributes
- Fix gson reflection bug
- Update dependencies
- Upgrade gradle wrapper

## Version 0.9.4 - 21.02.2022
- Add option to await on closing Backtrace Client

## Version 0.9.3 - 14.07.2021
- Improve the queue handling to eliminate rare deadlocks occurred during closing BacktraceClient

## Version 0.9.2 - 04.05.2021
- Fix the [issue](https://github.com/backtrace-labs/backtrace-java/issues/4) with excessive CPU usage by the backtrace-thread

## Version 0.9.1 - 14.04.2020
- Filter out stack traces from 'org.apache.log4j' and 'org.apache.logging'

## Version 0.9.0 - 23.03.2020
- First release.
