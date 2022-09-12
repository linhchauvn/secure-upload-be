-- DROP SCHEMA IF EXISTS "admin";

-- CREATE SCHEMA "admin";

-- "admin2".tsu_user_notification definition

-- DROP TABLE "admin2".tsu_user_notification;

CREATE TABLE IF NOT EXISTS tsu_user_notification (
                                id uuid not null,
                                user_id uuid not null,
                                is_read bool,
                                insert_time timestamp,
                                action_type varchar(255) not null, -- ASSIGN,DELETE,UPLOAD,EDIT,REQUESTESIGN,CUSTOMERESIGN,OOO;
                                action_object text,
                                action_author text,
                                old_value text,
                                new_value text,
                                CONSTRAINT tsu_user_notification_pkey PRIMARY KEY (id)
                                );
