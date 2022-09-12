-- DROP SCHEMA IF EXISTS "admin2";

-- CREATE SCHEMA "admin2";

-- "admin2".tsu_client definition

-- DROP TABLE "admin2".tsu_client;

CREATE TABLE IF NOT EXISTS tsu_client (
    id varchar(255) NOT NULL,
    locale varchar(255) NULL,
    "name" varchar(255) NULL,
    nav_colour varchar(255) NULL,
    CONSTRAINT tsu_client_pkey PRIMARY KEY (id)
    );


-- "admin2".tsu_conformity definition

-- DROP TABLE "admin2".tsu_conformity;

CREATE TABLE IF NOT EXISTS  tsu_conformity (
    "conformed-norms" varchar(50) NULL,
    "conformed-norms-index" int8 NULL
    );


-- "admin2".tsu_organization definition

-- DROP TABLE "admin2".tsu_organization;

CREATE TABLE IF NOT EXISTS  tsu_organization (
    id varchar(255) NOT NULL,
    email_address varchar(255) NULL,
    locale varchar(255) NULL,
    "name" varchar(255) NULL,
    CONSTRAINT tsu_organization_pkey PRIMARY KEY (id)
    );


-- "admin2".tsu_user definition

-- DROP TABLE "admin2".tsu_user;

CREATE TABLE IF NOT EXISTS  tsu_user (
    id varchar(255) NOT NULL,
    email_address varchar(255) NULL,
    is_ooo bool NULL,
    locale varchar(255) NULL,
    must_change_password bool NULL,
    "name" varchar(255) NULL,
    password_hash varchar(255) NULL,
    roles varchar(255) NULL,
    username varchar(255) NULL,
    organization varchar(255) NULL,
    CONSTRAINT tsu_user_pkey PRIMARY KEY (id),
    CONSTRAINT uk_egnnw51f1p3go1kx45l4na5hl UNIQUE (username),
    CONSTRAINT fk4tcifmwy02mvv8ns2kflqytky FOREIGN KEY (organization) REFERENCES tsu_organization(id)
    );
