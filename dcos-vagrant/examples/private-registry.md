# Using the Private Docker Registry

DC/OS Vagrant can optionally be deployed with a private [Docker registry](https://docs.docker.com/registry/) running on the bootstrap (`boot`) machine. This functionality must be enabled at deploy time.

The following steps demonstrate how to enable and use the private Docker registry to deploy Nginx:

1. Enable the private registry:

    ```bash
    $ export DCOS_PRIVATE_REGISTRY=true
    ```

1. [Deploy DC/OS Vagrant](/docs/deploy.md)

1. SSH into one of the machines:

    ```bash
    $ vagrant ssh boot
    ```

1. Download nginx from Docker Hub:

    ```bash
    $ docker pull nginx
    ```

1. Retag the nginx image:

    ```bash
    $ docker tag $(docker images | grep -m 1 ^nginx.*latest | awk -v N=3 '{print $N}') boot.dcos:5000/nginx
    ```

1. Upload nginx to the private registry:

    ```bash
    $ docker push boot.dcos:5000/nginx
    ```

1. Install the DC/OS CLI:

    ```bash
    $ curl https://raw.githubusercontent.com/dcos/dcos-vagrant/master/ci/dcos-install-cli.sh | bash
    ```

    For CLI reference, see [Installing the DC/OS CLI](https://docs.io/latest/usage/cli/)

1. Prepare a DC/OS service definition:

    ```bash
    $ tee nginx-marathon.json <<-'EOF'
    {
      "id": "/nginx",
      "instances": 1,
      "cpus": 0.5,
      "mem": 128,
      "container": {
        "type": "DOCKER",
        "docker": {
          "image": "boot.dcos:5000/nginx",
          "network": "HOST"
        }
      },
      "healthChecks": [
        {
          "protocol": "COMMAND",
          "command": { "value": "service nginx status | grep -q 'nginx is running.'"},
          "gracePeriodSeconds": 300,
          "intervalSeconds": 60,
          "timeoutSeconds": 20,
          "maxConsecutiveFailures": 3
        }
      ],
      "labels": {
        "DCOS_SERVICE_NAME": "nginx",
        "DCOS_SERVICE_SCHEME": "http",
        "DCOS_SERVICE_PORT_INDEX": "0"
      }
    }
    EOF
    ```

1. Create a DC/OS service:

    ```bash
    $ dcos marathon app add nginx-marathon.json
    ```

    If auth is enabled, authenticate as instructed by the CLI.
1. Lookup the nginx container IP

    With the DC/OS CLI and jq:

    ```bash
    NGINX_IP=$(dcos marathon app show nginx | jq -r .tasks[0].host)
    ```

    OR With Mesos-DNS and dig:

    ```bash
    sudo yum install bind-utils -y
    NGINX_IP=$(dig +short @m1.dcos nginx.marathon.mesos)
    ```

    OR with the Mesos-DNS HTTP API and jq:

    ```bash
    NGINX_IP=$(curl --fail --location --silent --show-error m1.dcos:8123/v1/hosts/nginx.marathon.mesos | jq -r .[0].ip)
     ```
    This step is necessary from the boot machine (but not the masters or agents), because configuring it to resolve using Mesos-DNS would create a dependency cycle.

1. Test the nginx endpoint with curl

    ```bash
    $ curl ${NGINX_IP}
    <!DOCTYPE html>
    <html>
    <head>
    <title>Welcome to nginx!</title>
    <style>
        body {
            width: 35em;
            margin: 0 auto;
            font-family: Tahoma, Verdana, Arial, sans-serif;
        }
    </style>
    </head>
    <body>
    <h1>Welcome to nginx!</h1>
    <p>If you see this page, the nginx web server is successfully installed and
    working. Further configuration is required.</p>

    <p>For online documentation and support please refer to
    <a href="http://nginx.org/">nginx.org</a>.<br/>
    Commercial support is available at
    <a href="http://nginx.com/">nginx.com</a>.</p>

    <p><em>Thank you for using nginx.</em></p>
    </body>
    </html>
    ```
