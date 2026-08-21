-- Idempotent creation of the least-privilege application login/user used by
-- the backend service (principe de moindre privilège : le backend ne doit
-- plus se connecter en 'sa').
--
-- Runs after create-db (la base padeldb doit exister) and before backend
-- (le backend se connecte avec ce compte des son demarrage).
--
-- db_ddladmin est necessaire car spring.jpa.hibernate.ddl-auto=update laisse
-- Hibernate creer/modifier les tables au demarrage du backend ; sans ce role,
-- le tout premier demarrage contre un schema vide echouerait.

USE master;
GO

IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'padel_app')
BEGIN
    CREATE LOGIN padel_app WITH PASSWORD = 'PadelApp2026!';
END
GO

USE padeldb;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'padel_app')
BEGIN
    CREATE USER padel_app FOR LOGIN padel_app;
END
GO

IF IS_ROLEMEMBER('db_datareader', 'padel_app') = 0
    ALTER ROLE db_datareader ADD MEMBER padel_app;
GO

IF IS_ROLEMEMBER('db_datawriter', 'padel_app') = 0
    ALTER ROLE db_datawriter ADD MEMBER padel_app;
GO

IF IS_ROLEMEMBER('db_ddladmin', 'padel_app') = 0
    ALTER ROLE db_ddladmin ADD MEMBER padel_app;
GO