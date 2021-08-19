# Spark to Solace

This repository shows how you can write to Solace from

// https://zpjiang.me/2015/10/17/zsh-no-match-found-local-spark/
// https://databricks.gitbooks.io/databricks-spark-knowledge-base/content/troubleshooting/missing_dependencies_in_jar_files.html

$SPARK_HOME/bin/spark-submit \
    --class "App" \
    --master "local[4]" \
    target/spark-to-solace-1.0-SNAPSHOT.jar