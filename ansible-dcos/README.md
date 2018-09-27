DESCRIPTION
    This project automatically install and deploy Big Data services on an existing cluster.


REQUIREMENTS
   An existing cluster is required.


CONFIGURATION
    ./inventory : IP of the 7 machines to use, cluster credentials (user/pass).

USAGE
    $> ./saas.sh or ./saas.sh <config_file> and follow the DC/OS security authentification instruction at the beginning.


NOTES & ISSUES
    Do not Ctrl+C even when a message display to do it to finish faster. Simply let the script run and wait to avoid issues because services running on Kubernetes can not be completely up.


BIG DATA SERVICES (see ./plays/main.yml)
    Kubernetes orchestrator:
      - Kubernetes

    Packages running on Mesos DC/OS orchestrator:
      - ElasticSearch
      - Kafka
      - Logstash
      - Kibana

    Packages running on Kubernetes:
      - Flink

SOURCE
    Based on the official ansible-dcos project https://github.com/dcos-labs/ansible-dcos.
