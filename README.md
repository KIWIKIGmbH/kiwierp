## kiwierp

The kiwierp is an open source ERP system, especially for "hardware companies". The goal of this project is to manage all of your business for hareware stuffs within one system. It is made of AngularJS and Twitter Bootstrap 3 for the frontend and Play Framework 2.3 and PostgreSQL for the backend.

### Requirements

#### Frontend
* Node.js (v0.11.x)
* npm
* [Grunt](http://gruntjs.com/)
* [Bower](http://bower.io/)

#### Backend
* JDK 8
* sbt (0.13.x)
* PostgreSQL

### Installation

As an simple example, I will show you how to install the kiwierp on Ubuntu 12.04 LTS, using one Nginx server.

#### Preparation

Installation of the requirements above (assume Postgres is already prepared).

* Nodejs and npm

```sh
$ cd /tmp
$ # Download Nodejs
$ curl -LO http://nodejs.org/dist/v0.11.13/node-v0.11.13-linux-x64.tar.gz
$ tar xvzf node-v0.11.13-linux-x64.tar.gz -C /usr/local
$ ln -s /usr/local/node-v0.11.13-linux-x64 /usr/local/node
$ export PATH=/usr/local/node/bin:$PATH
$ rm node-v0.11.13-linux-x64.tar.gz
```

* Grunt and Bower

```sh
$ npm install -g bower grunt
```

* JDK 8

```sh
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install oracle-java8-installer
$ export JAVA_HOME=/usr/lib/jvm/java-8-oracle
```

* sbt

```sh
$ cd /tmp
$ # Download sbt
$ curl -LO https://dl.bintray.com/sbt/native-packages/sbt/0.13.5/sbt-0.13.5.zip
$ unzip sbt-0.13.5.zip -d /usr/local
$ export PATH=/usr/local/sbt/bin:$PATH
$ rm sbt-0.13.5.zip
```

#### Running kiwierp

* Download from GitHub

```sh
$ cd /tmp
$ git clone https://github.com/KIWIKIGMBH/kiwierp
$ cd ./kiwierp
```

* Nginx

Setup the Nginx configuration file like below:

```
upstream kiwierp-backend {
    server 127.0.0.1:9000;
}

server {
    listen         80;
    server_name    localhost;

    location / {
        root         /path/to/frontend;
        try_files    $uri   /index.html;

        rewrite    fonts/(.*)$      /fonts/$1      break;
        rewrite    images/(.*)$     /images/$1     break;
        rewrite    scripts/(.*)$    /scripts/$1    break;
        rewrite    styles/(.*)$     /styles/$1     break;

    }

    location /api/v1 {
        proxy_pass    http://kiwierp-backend;
    }
}
```

* Setup Frontend

```sh
$ cd /tmp/kiwierp/kiwierp-frontend
$ npm install
$ bower install
$ grunt build
$ cp -R dist /path/to/frontend
```

* Setup Database and Backend

```sh
$ cd /tmp/kiwierp
$ cp -R kiwierp-backend /path/to/backend
$ cd /path/to/backend
$ # Create new postgres user
$ createuser -P kiwierp
$ # Create new database
$ createdb -E utf8 -O kiwierp kiwierp
$ # Read kiwierp.v1.sql
$ psql -f schema/kiwierp.v1.sql -U kiwierp kiwierp
$ # Export some environment variables
$ export APPLICATION_SECRET="application_secret"
$ export APPLICATION_salt="application_salt"
$ export DB_DEFAULT_DRIVER="org.postgresql.Driver"
$ export DB_DEFAULT_URL="jdbc:postgresql://localhost:5432/kiwierp"
$ export DB_DEFAULT_USER="kiwierp"
$ export DB_DEFAULT_PASSWORD="password"
$ # Compile
$ sbt compile
$ # Running backend (You can stop sbt console typing Ctrl-D.)
$ sbt start
```

* Cleanup /tmp directory
```sh
$ cd /tmp
$ rm -Rf kiwierp
```

* Running

```sh
$ sudo /etc/init.d/nginx start
```

### Features

#### Current
* Management for the inventories of the hardware stuffs.

#### Future
* Features like hardware installation backlog, manufacuture management, financial overview, reporting tool for all of the data, and more.

### Roadmap

#### Version 0.1
* Fix some bugs
* Error handlings
* Test codes for the frontend and the backend.
* Dashboard
* The feature of inventory consumption
* Restructure

### License

This project is licensed under the MPL2 license, see LICENSE file.

The portion of [SB Admin 2](https://github.com/IronSummitMedia/startbootstrap/tree/gh-pages/templates/sb-admin-2) is used for some view templates. The SB Admin 2 is under the Apache Software License v2.0, see the link [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
