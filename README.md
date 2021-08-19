# Spark to Solace

This repository shows how you can write to Solace from Spark partitions. Here's a visualization of what this pattern looks like:

![Spark to Solace architecture](/docs/architecture.png)

[Line 71 in App.java](/src/main/java/App.java#L64) shows how you'd use row data in a Solace topic.

## Running the example

Download Apache Spark: https://spark.apache.org/downloads.html

Clone this repository:

```
git clone https://github.com/andrew-j-roberts/spark-to-solace.git
cd spark-to-solace
```

Run this CLI command:

```bash
$SPARK_HOME/bin/spark-submit \
    --class "App" \
    --master "local[4]" \
    target/spark-to-solace-1.0-SNAPSHOT.jar
```

## Resources

- Solace topic architecture best practices: https://solace.com/blog/topic-hierarchy-best-practices/
- Solace developers site: https://www.solace.dev/
- Issue I ran into running Spark CLI:  
   https://zpjiang.me/2015/10/17/zsh-no-match-found-local-spark/
- Issue I ran into running Spark jar:  
   https://databricks.gitbooks.io/databricks-spark-knowledge-base/content/troubleshooting/missing_dependencies_in_jar_files.html
