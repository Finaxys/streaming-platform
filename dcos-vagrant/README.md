DESCRIPTION
    This project quickly provision a cluster on a local machine.

REQUIREMENTS
   - vagrant v1.9.1+
   - ansible v2.1.4+
   - virtualbox v5.2.12+
   - java v1.8
   - python 2.7.14
   - 32GB computer.

CONFIGURATION
    ./VagrantConfig.yaml : It contains by default 1 master, 4 private agent, 1 public agent and 1 boot (total of 7 machines) to run perfectly with the automation project "ansible-dcos". This file contains the configured machine to be deployed in the mini-cluster (a clusterproduction like, running locally).

USAGE
    If you already had machines deployed, then :
    $> vagrant destroy;

    If you want to build the cluster :
    $> vagrant up;

SOURCE
    Based on the official dcos-vagrant project https://github.com/dcos-labs/dcos-vagrant.
