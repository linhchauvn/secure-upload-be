
ALTER TABLE tsu_audit ADD COLUMN id uuid NOT NULL;
ALTER TABLE tsu_audit ADD COLUMN last_updated timestamp;
ALTER TABLE tsu_audit ADD CONSTRAINT tsu_audit_pkey PRIMARY KEY (id);