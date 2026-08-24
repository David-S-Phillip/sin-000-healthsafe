The Problem
You have a messy CSV file (wards-outdated.csv) sitting in ingestion-service/src/main/resources/.

If a real hospital database tries to use raw CSV data directly, it will crash or corrupt records because humans entered dirty data. Some rows have extra spaces, missing values, uppercase vs. lowercase mismatches, or invalid numbers (like writing "five" instead of 5).

