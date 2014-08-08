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
  user_id integer NOT NULL UNIQUE,
  expires_in integer NOT NULL,
  token_type character varying (32) NOT NULL,
  created_at timestamp with time zone NOT NULL,
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

CREATE TABLE parts (
  id serial PRIMARY KEY,
  product_id integer NOT NULL,
  name character varying (128) NOT NULL,
  description character varying (512),
  needed_quantity integer NOT NULL,
  unclassified integer NOT NULL,
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
  parts_id integer NOT NULL,
  description character varying (512),
  quantity integer NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (parts_id)
    REFERENCES parts (id) MATCH SIMPLE
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

CREATE TABLE inventory_consumption (
  id serial PRIMARY KEY,
  product_id integer NOT NULL,
  consumed integer NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (product_id)
  REFERENCES products (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TYPE inventory_order_status AS ENUM ('ordered', 'shipped', 'delivered');

CREATE TABLE inventory_orders (
  id serial PRIMARY KEY,
  parts_id integer NOT NULL,
  supplier_id integer NOT NULL,
  ordered integer NOT NULL,
  ordered_date timestamp with time zone NOT NULL,
  shipped_date timestamp with time zone,
  delivered_date timestamp with time zone,
  status inventory_order_status NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  FOREIGN KEY (parts_id)
    REFERENCES parts (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (supplier_id)
    REFERENCES suppliers (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TYPE field_type AS ENUM ('integer', 'decimal', 'string', 'timestamp', 'boolean');

CREATE TABLE inventory_fields (
  id serial PRIMARY KEY,
  product_id integer NOT NULL,
  name character varying (128) NOT NULL,
  field_type field_type NOT NULL,
  is_required boolean NOT NULL,
  min integer,
  max integer,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  UNIQUE (product_id, name),
  FOREIGN KEY (product_id)
    REFERENCES products (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE inventory_field_values (
  inventory_id integer NOT NULL,
  inventory_field_id integer NOT NULL,
  value character varying (512) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  deleted_at timestamp with time zone,
  PRIMARY KEY (inventory_id, inventory_field_id),
  FOREIGN KEY (inventory_id)
    REFERENCES inventories (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (inventory_field_id)
    REFERENCES inventory_fields (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);