-- user table
INSERT INTO tsu_user (id, email_address, is_ooo, locale, must_change_password, "name", password_hash, roles, username) VALUES
                    ('41189a5f-2d1c-42cc-b46e-fa2816bd49fc', 'admin@tenerity.com', false, 'sv', false, 'Admin', '$2a$10$tc7v0HQWAnZfa.0St9uDrOmmBj5MsFjIcUlg.4cbRYgW6dnNT46S2', 'admin', 'admin'),
                    ('02ba78d2-d376-4b44-94d6-a9435cc10fca', 'agent@tenerity.com', false, 'sv', false, 'Agent', '$2a$10$srtqCquzxI3Q2b9mKyTpSukBOnWsB7V46siz5TnwhAkHFLtSKro..', 'agent', 'agent'),
                    ('1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', 'agent.peter@tenerity.com', false, 'sv', false, 'peter', '$2a$10$Gqen811mRv.A4.8jcSvl/OTybw/hlgnwodtm.T71TxanEXqvk5ope', 'agent', 'peter'),
                    ('8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', 'agent.bill@tenerity.com', false, 'sv', false, 'bill', '$2a$10$9BWsi1hMzOqMQeCYSLOcmeNc4ghsVYsxpXZNDWTP6r2Q51L1IO8yC', 'agent', 'bill'),
                    ('f7c62649-f3d2-4b57-972f-2a11387349aa', 'agent.mary@tenerity.com', false, 'sv', false, 'mary', '$2a$10$MTdfCijTUTKTh6DxMPEsB.9c.AT4.XugVk1ozUhwrF09giQDoz3ou', 'agent', 'mary'),
                    ('ec2fb546-7612-4b77-b4ec-fa191406ce24', 'thirdparty@tenerity.com', false, 'sv', false, 'Tenerity', '$2a$10$nGwqfkMO0zJjmVMe79HSa..dOEzwp6Gw8MbjOEONZv7FlULhEbrBC', 'third-party', 'tenerity'),
                    ('1a5a71f5-0bf2-42da-9dbe-8503ec16e12c', 'thirdparty@citibank.com', false, 'da', false, 'Citibank', '$2a$10$g4DoC4qHQQ9oGjMlufQQM.xFRHyMT7vfEFWDBlnWTmmsyUw6bokca', 'third-party', 'citibank');


-- organization
INSERT INTO tsu_organization (email_address, id, locale, name) VALUES
                             ('austrax@gmail.com', '3a89cd24-903b-4007-8b5d-e2c46bfb29d6', 'sv', 'Austrax'),
                             ('citibank@gmail.com', '10282a7d-637d-4218-a360-7e14cd912906', 'da', 'Citibank'),
                             ('nordvpn@gmail.com', '4a818e5a-b189-4de5-8014-cb1cf9550a3b', 'no', 'NordVpn'),
                             ('tenerity@gmail.com', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545', 'fi', 'Tenerity'),
                             ('xerox@gmail.com', 'f2bafeb8-1427-41a1-b905-f2e06c07a08b', 'sv', 'Xerox');

-- client
INSERT INTO tsu_client (id, locale, name, nav_colour) VALUES
                        ('78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'sv', 'Alice', '#FF0000'),
                        ('0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'da', 'Bob', '#FF7F00'),
                        ('4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'no', 'Claire', '#FFFF00'),
                        ('8d4ed9c1-b797-4730-a41c-020079112e95', 'fi', 'Damon', '#00FF00');

-- case table
INSERT INTO tsu_case (id, superoffice_id, assigned_agent, client, customer_email, last_updated, status) VALUES
    ('517635db-5ef4-4e34-b83e-f8cb2af5c871', 'SO21-001', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'alice@hotmail.com', CURRENT_DATE, 'status/closed'),
    ('d9936b00-45c5-4c2f-a13f-9f3097e18f77', 'SO21-002', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'bob@coolmail.net', DATEADD('DAY',-01, CURRENT_DATE), 'status/closed'),
    ('58f7a45c-2ede-46dd-8ce2-057b463dcebe', 'SO21-003', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'claire@synthentic.au', DATEADD('DAY',-12, CURRENT_DATE), 'status/closed'),
    ('7c8777b3-2f0d-4815-b596-1a0871267696', 'SO21-004', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '8d4ed9c1-b797-4730-a41c-020079112e95', 'damon@territory.in', DATEADD('DAY',-11, CURRENT_DATE), 'status/closed'),
    ('0da01431-882b-4bd9-ab1b-d9ea5dc5b86e', 'SO21-005', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'alice@hotmail.com', DATEADD('DAY',-10, CURRENT_DATE), 'status/closed'),
    ('280aa369-3c52-4786-872c-d86255ca434b', 'SO21-006', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'bob@coolmail.net', DATEADD('DAY',-09, CURRENT_DATE), 'status/closed'),
    ('f473a471-c542-4b90-89cb-422c7a606439', 'SO21-007', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'claire@synthentic.au', DATEADD('DAY',-08, CURRENT_DATE), 'status/closed'),
    ('0600a7c9-1311-4953-8776-72f36d89052d', 'SO21-008', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '8d4ed9c1-b797-4730-a41c-020079112e95', 'damon@territory.in', DATEADD('DAY',-07, CURRENT_DATE), 'status/closed'),
    ('69b89b5c-85b8-4700-a376-355642b6652f', 'SO21-009', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'alice@hotmail.com', DATEADD('DAY',-06, CURRENT_DATE), 'status/closed'),
    ('4919ca3c-2660-456a-85a7-fbea3c13cec7', 'SO21-010', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'bob@coolmail.net', DATEADD('DAY',-06, CURRENT_DATE), 'status/open'),
    ('ec2cd957-864f-4f8f-bc81-a666f6b02f18', 'SO21-011', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'claire@synthentic.au', DATEADD('DAY',-05, CURRENT_DATE), 'status/open'),
    ('db28848c-20e5-4165-aa89-745eba2cf024', 'SO21-012', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '8d4ed9c1-b797-4730-a41c-020079112e95', 'damon@territory.in', DATEADD('DAY',-05, CURRENT_DATE), 'status/open'),
    ('bf892e73-6b73-47a2-9703-6bb5c35d18f2', 'SO21-013', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'alice@hotmail.com', DATEADD('DAY',-04, CURRENT_DATE), 'status/open'),
    ('4fd153e9-c947-4fa0-a61d-f9e114870807', 'SO21-014', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'bob@coolmail.net', DATEADD('DAY',-04, CURRENT_DATE), 'status/open'),
    ('63263a61-6720-45fc-91b1-f3f2d989cac6', 'SO21-015', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'claire@synthentic.au', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('9b25c84c-e7c9-4784-b402-802cd635ce32', 'SO21-016', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '8d4ed9c1-b797-4730-a41c-020079112e95', 'damon@territory.in', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('b3ccd4af-96b5-401e-8110-524134058b70', 'SO21-017', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'alice@hotmail.com', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('6d36dab8-de24-4e9c-83c7-ffda26b4f9e7', 'SO21-018', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'bob@coolmail.net', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('09feff5b-889f-4eed-9615-68c01bea67f4', 'SO21-019', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'claire@synthentic.au', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('648e8bcf-0bb4-461d-b0e0-46361a923bc1', 'SO21-020', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '8d4ed9c1-b797-4730-a41c-020079112e95', 'lucas@territory.in', DATEADD('DAY',-03, CURRENT_DATE), 'status/open'),
    ('39680e25-9926-4d44-b8f8-7c48432062ec', 'SO21-021', '02ba78d2-d376-4b44-94d6-a9435cc10fca', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'leon@gmail.com', DATEADD('DAY',-02, CURRENT_DATE), 'status/closed'),
    ('79928676-3151-4c87-a989-d76847306aa3', 'SO21-022', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'phantom@dksh.com', DATEADD('DAY',-01, CURRENT_DATE), 'status/closed'),
    ('f6c3bff2-d17c-4516-870f-1a46cd4f421c', 'SO21-023', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'lucas@territory.in', DATEADD('DAY',-01, CURRENT_DATE), 'status/closed'),
    ('dbdcd605-2e5f-4dbb-be7e-87a4bb62a5a5', 'SO21-024', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '8d4ed9c1-b797-4730-a41c-020079112e95', 'leon@gmail.com', DATEADD('DAY',-01, CURRENT_DATE), 'status/open'),
    ('4e337650-cebf-4342-870a-feee50f42c55', 'SO21-025', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'damon@territory.in', DATEADD('DAY',-01, CURRENT_DATE), 'status/open'),
    ('2b634b7b-2287-41fe-a60f-9ef51d8badbe', 'SO21-026', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'lucas@territory.in', DATEADD('DAY',-01, CURRENT_DATE), 'status/open'),
    ('77cf41bf-27d9-415f-ba61-3551323c0e71', 'SO21-027', '1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', '4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'leon@gmail.com', DATEADD('DAY',-01, CURRENT_DATE), 'status/open'),
    ('ab02b2fc-16f5-4475-aad8-745598511618', 'SO21-028', '8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', '8d4ed9c1-b797-4730-a41c-020079112e95', 'phantom@dksh.com', CURRENT_DATE, 'status/open'),
    ('71597560-031d-406c-aa42-12a67af56295', 'SO21-029', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'damon@territory.in', CURRENT_DATE, 'status/open'),
    ('9c93138b-d4bb-4fac-b5c0-6233a419c8e8', 'SO21-030', 'f7c62649-f3d2-4b57-972f-2a11387349aa', '0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'phantom@dksh.com', CURRENT_DATE, 'status/open');

-- workspace
INSERT INTO tsu_workspace (id, case_id, third_party_id) VALUES
                          ('a14ab315-4853-4149-8331-1579351f45b4', '517635db-5ef4-4e34-b83e-f8cb2af5c871', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545'),
                          ('81c6eddc-5eb8-4171-8ef3-72d4357e3fd4', 'b3ccd4af-96b5-401e-8110-524134058b70', '3a89cd24-903b-4007-8b5d-e2c46bfb29d6'),
                          ('9f5da80e-fe04-4b2e-bba4-da63cba06bdf', '58f7a45c-2ede-46dd-8ce2-057b463dcebe', '10282a7d-637d-4218-a360-7e14cd912906'),
                          ('783a48a0-8732-4ea4-ae9a-86723118e6ad', '0da01431-882b-4bd9-ab1b-d9ea5dc5b86e', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545'),
                          ('575f1f3b-cc6d-40d6-8d2d-cc2ecb4fd1af', 'f473a471-c542-4b90-89cb-422c7a606439', '3a89cd24-903b-4007-8b5d-e2c46bfb29d6'),
                          ('496c4dba-d21e-4908-9a90-f6d2723e133b', '69b89b5c-85b8-4700-a376-355642b6652f', '10282a7d-637d-4218-a360-7e14cd912906'),
                          ('b8bac49c-a04c-4d24-a5f2-1e07012a835a', 'ec2cd957-864f-4f8f-bc81-a666f6b02f18', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545'),
                          ('694e9a78-ded8-49c5-b34e-09731a9e62be', 'bf892e73-6b73-47a2-9703-6bb5c35d18f2', '4a818e5a-b189-4de5-8014-cb1cf9550a3b'),
                          ('9630b090-bfd0-472e-aafd-681c64b8cf91', '63263a61-6720-45fc-91b1-f3f2d989cac6', '10282a7d-637d-4218-a360-7e14cd912906'),
                          ('28e97fe1-48a0-499a-810f-b251810d907f', '648e8bcf-0bb4-461d-b0e0-46361a923bc1', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545');