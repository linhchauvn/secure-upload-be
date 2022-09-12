-- organization
INSERT INTO tsu_organization (email_address, id, locale, name)
VALUES ('austrax@gmail.com', '3a89cd24-903b-4007-8b5d-e2c46bfb29d6', 'sv', 'Austrax'),
       ('citibank@gmail.com', '10282a7d-637d-4218-a360-7e14cd912906', 'da', 'Citibank'),
       ('nordvpn@gmail.com', '4a818e5a-b189-4de5-8014-cb1cf9550a3b', 'no', 'NordVpn'),
       ('tenerity@gmail.com', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545', 'fi', 'Tenerity'),
       ('xerox@gmail.com', 'f2bafeb8-1427-41a1-b905-f2e06c07a08b', 'sv', 'Xerox');

-- user table
INSERT INTO tsu_user (id, email_address, is_ooo, locale, must_change_password, "name", organization, password_hash, roles, username)
VALUES ('41189a5f-2d1c-42cc-b46e-fa2816bd49fc', 'admin@tenerity.com', false, 'sv', false, 'Admin', null, 'admin1', 'admin', 'admin'),
       ('02ba78d2-d376-4b44-94d6-a9435cc10fca', 'agent@tenerity.com', false, 'sv', false, 'Agent', null, 'agent1', 'agent', 'agent'),
       ('1a7c89d8-6a8f-41d7-b87f-b40ffc8269b1', 'agent.peter@tenerity.com', false, 'sv', false, 'peter', null, 'peter1', 'agent', 'peter'),
       ('8e29f74b-e2e8-4a86-a5bd-e14e1b8ede9f', 'agent.bill@tenerity.com', false, 'sv', false, 'bill', null, 'bill1', 'agent', 'bill'),
       ('f7c62649-f3d2-4b57-972f-2a11387349aa', 'agent.mary@tenerity.com', false, 'sv', false, 'mary', null, 'mary1', 'agent', 'mary'),
       ('ec2fb546-7612-4b77-b4ec-fa191406ce24', 'thirdparty@tenerity.com', false, 'sv', false, 'Tenerity', '8a7dc83f-d89c-45f4-9ed5-60f2a5bdc545', 'tenerity1', 'third-party', 'tenerity'),
       ('1a5a71f5-0bf2-42da-9dbe-8503ec16e12c', 'thirdparty@citibank.com', false, 'da', false, 'Citibank', '10282a7d-637d-4218-a360-7e14cd912906', 'citibank1', 'third-party', 'citibank'),
       ('9a00cd1f-49a1-450a-8fda-7ba5b167cc24', 'admin.linh@austrax.com', false, 'sv', false, 'Linh C', null, 'linh1', 'admin', 'linh'),
       ('bd38cfbf-b969-46e6-a441-437a6f9c77b7', 'agent.nam@austrax.com', false, 'da', false, 'Nam V', null, 'nam1', 'agent', 'nam'),
       ('9da68c05-beba-4ae4-8b04-72397529ee10', 'agent.john@nordvpn.com', false, 'no', false, 'John D', null, 'john1', 'agent', 'john'),
       ('0cd972d7-735b-47bc-9c40-735528b226d7', 'agent.tom@tenerity.com', false, 'fi', false, 'Tom C', null, 'tom1', 'agent', 'tom'),
       ('033e1476-438e-4c31-a3db-a93af4b6d349', 'agent.jerry@tenerity.com', false, 'sv', false, 'Jerry M', null, 'jerry1', 'agent', 'jerry'),
       ('36135a49-0389-4311-a966-a4772c3054cc', 'agent.billy@tenerity.com', true, 'da', false, 'Billy F', null, 'billy1', 'agent', 'billy'),
       ('298dfb7f-2528-4f65-a906-ceca5d604c51', 'agent.annie@nordvpn.com', false, 'no', false, 'Annie O', '4a818e5a-b189-4de5-8014-cb1cf9550a3b', 'annie1', 'third-party', 'annie'),
       ('50fe5be9-3730-46f9-8c60-3e795c9c75b4', 'thirdparty.fergie@citibank.com', false, 'fi', false, 'Fergie B', '10282a7d-637d-4218-a360-7e14cd912906', 'fergie1', 'third-party', 'fergie'),
       ('2a588e9e-ef6c-412d-bda8-613d417e757d', 'thirdparty.xerox@xerox.com', true, 'sv', false, 'Xerox', 'f2bafeb8-1427-41a1-b905-f2e06c07a08b', 'xerox1', 'third-party', 'xerox'),
       ('632eeb26-c597-40bc-bb0a-ae17e8b0f6ed', 'thirdparty.lucas@xerox.com', true, 'da', false, 'Lucas X', 'f2bafeb8-1427-41a1-b905-f2e06c07a08b', 'lucas1', 'third-party', 'lucas');

-- client
INSERT INTO tsu_client (id, locale, name, nav_colour)
VALUES ('78caa4df-0f61-4ca4-b66d-fcbe45901d2f', 'sv', 'Alice', '#FF0000'),
       ('0cb60b26-e4b5-4d27-aa52-58dea11cd89f', 'da', 'Bob', '#FF7F00'),
       ('4c55bb69-89b0-4ec1-b2ff-78817f385c97', 'no', 'Claire', '#FFFF00'),
       ('8d4ed9c1-b797-4730-a41c-020079112e95', 'fi', 'Damon', '#00FF00');