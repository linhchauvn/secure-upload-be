-- case_management.tsu_audit definition

-- DROP TABLE case_management.tsu_audit;

CREATE TABLE IF NOT EXISTS tsu_audit (
                                   latest text NULL,
                                   local_ref text NULL,
                                   remote_addr text NULL,
                                   uri_accessed text NULL
);


-- case_management.tsu_case definition

-- DROP TABLE case_management.tsu_case;

CREATE TABLE IF NOT EXISTS tsu_case (
                                  assigned_agent uuid NULL,
                                  client uuid NULL,
                                  code text NULL,
                                  customer_date_of_birth text NULL,
                                  customer_email text NULL,
                                  customer_first_name text NULL,
                                  customer_last_name text NULL,
                                  customer_national_id text NULL,
                                  customer_token_hash text NULL,
                                  documents_expunged bool NULL,
                                  id uuid NOT NULL,
                                  need_agent_notification bool NULL,
                                  status text NULL,
                                  superoffice_id text NULL,
                                  last_updated timestamp NULL,
                                  CONSTRAINT tsu_case_pkey PRIMARY KEY (id),
                                  CONSTRAINT tsu_case_uniques UNIQUE (code)
);


-- case_management.tsu_workspace definition

-- DROP TABLE case_management.tsu_workspace;

CREATE TABLE IF NOT EXISTS tsu_workspace (
                                       bank_id_login bool NULL,
                                       belongs_to_customer bool NULL,
                                       code text NULL,
                                       customer_token_hash text NULL,
                                       documents text NULL, -- list of document.id, separate by comma
                                       id uuid NOT NULL,
                                       "label" text NULL,
                                       third_party_id uuid NULL,
                                       third_party_ref uuid NULL,
                                       case_id uuid NULL,
                                       last_access timestamp NULL,
                                       CONSTRAINT tsu_workspace_pkey PRIMARY KEY (id),
                                       CONSTRAINT tsu_workspace_uniques UNIQUE (code),
                                       CONSTRAINT fkr_case FOREIGN KEY (case_id) REFERENCES case_management.tsu_case(id)
);

-- Column comments

COMMENT ON COLUMN tsu_workspace.documents IS 'list of document.id, separate by comma';
