-- "document".tsu_document definition

-- DROP TABLE "document".tsu_document;

CREATE TABLE IF NOT EXISTS tsu_document (
                                     content_type text NULL,
                                     filename text NULL,
                                     id uuid NOT NULL,
                                     is_e_signed bool NULL,
                                     "key" varchar NULL,
                                     "label" text NULL,
                                     mark_as_read bool NULL,
                                     needs_e_signature bool NULL,
                                     originator_ref uuid NULL,
                                     originator_type varchar NULL,
                                     signicat_request_id text NULL,
                                     signicat_task_id text NULL,
                                     case_id uuid NULL,
                                     file_path text NULL,
                                     CONSTRAINT tsu_document_pkey PRIMARY KEY (id)
);

-- DROP TYPE "document"."key_document";

-- CREATE TYPE "key_document" AS ENUM (
-- 	'poi',
-- 	'poa',
-- 	'casedoc-1',
-- 	'123');

-- DROP TYPE "document"."loc";

-- CREATE TYPE "document"."loc" AS ENUM (
-- 	'sv',
-- 	'da',
-- 	'no',
-- 	'fi');

-- DROP TYPE "document"."originatortype_document";

-- CREATE TYPE "document"."originatortype_document" AS ENUM (
-- 	'originator-type/customer',
-- 	'originator-type/agent',
-- 	'originator-type/third-party');

-- DROP TYPE "document"."role_organization";

-- CREATE TYPE IF NOT EXISTS "role_organization" AS ENUM (
-- 	'agent',
-- 	'third-party');

-- DROP TYPE "document"."role_user";

-- CREATE TYPE IF NOT EXISTS "role_user" AS ENUM (
-- 	'admin',
-- 	'agent',
-- 	'third-party');

-- DROP TYPE "document"."status_case";

-- CREATE TYPE IF NOT EXISTS "document"."status_case" AS ENUM (
-- 	'status/open',
-- 	'status/closed');