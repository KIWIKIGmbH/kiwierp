---
--- Basic
---
CREATE TYPE user_type AS ENUM ('admin', 'user', 'guest');

CREATE TABLE users (
  id serial PRIMARY KEY,
  name character varying (64) NOT NULL UNIQUE,
  password character varying (512) NOT NULL,
  user_type user_type NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone
);

CREATE TABLE user_scopes (
  permitted_user_types user_type[] NOT NULL,
  method character varying (16) NOT NULL,
  uri character varying (64) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  PRIMARY KEY (method, uri)
);

CREATE TABLE access_tokens (
  token character varying (128) PRIMARY KEY,
  user_id integer NOT NULL,
  expires_in integer NOT NULL,
  token_type character varying (32) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

---
--- Inventories
---
CREATE TABLE products (
  id serial PRIMARY KEY,
  name character varying (128) NOT NULL UNIQUE,
  description character varying (512),
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone
);

CREATE TABLE components (
  id serial PRIMARY KEY,
  product_id integer NOT NULL,
  name character varying (128) NOT NULL,
  description character varying (512),
  needed_quantity integer NOT NULL,
  unclassified_quantity integer NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  UNIQUE (product_id, name),
  FOREIGN KEY (product_id)
    REFERENCES products (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE inventories (
  id serial PRIMARY KEY,
  component_id integer NOT NULL,
  description character varying (512),
  quantity integer NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (component_id)
    REFERENCES components (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE suppliers (
  id serial PRIMARY KEY,
  company_name character varying (128) NOT NULL,
  personal_name character varying (128) NOT NULL,
  phone_number character varying (128) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone
);

CREATE TYPE inventory_order_status AS ENUM ('ordered', 'shipped', 'delivered');

CREATE TABLE orders (
  id serial PRIMARY KEY,
  component_id integer NOT NULL,
  supplier_id integer NOT NULL,
  quantity integer NOT NULL,
  ordered_date timestamp with time zone NOT NULL,
  shipped_date timestamp with time zone,
  delivered_date timestamp with time zone,
  status inventory_order_status NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (component_id)
    REFERENCES components (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (supplier_id)
    REFERENCES suppliers (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/sessions', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'admin'::user_type), 'GET', '/users', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'admin'::user_type), 'POST', '/users', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'GET', '/users/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'admin'::user_type), 'PATCH', '/users/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'admin'::user_type), 'DELETE', '/users/:id', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/products', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/products', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/products/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'PATCH', '/inventory-management/products/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'DELETE', '/inventory-management/products/:id', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/components', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/components', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/components/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'PATCH', '/inventory-management/components/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'DELETE', '/inventory-management/components/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/components/:id/classification', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/suppliers', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/suppliers', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/suppliers/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'PATCH', '/inventory-management/suppliers/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'DELETE', '/inventory-management/suppliers/:id', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/inventories', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/inventories', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/inventories/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'PATCH', '/inventory-management/inventories/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'DELETE', '/inventory-management/inventories/:id', now(), now());

INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/orders', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'POST', '/inventory-management/orders', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'guest'::user_type), 'GET', '/inventory-management/orders/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'PATCH', '/inventory-management/orders/:id', now(), now());
INSERT INTO user_scopes (permitted_user_types, method, uri, created_at, updated_at) VALUES (enum_range('admin'::user_type, 'user'::user_type), 'DELETE', '/inventory-management/orders/:id', now(), now());
