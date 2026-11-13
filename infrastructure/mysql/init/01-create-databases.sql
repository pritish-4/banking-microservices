CREATE DATABASE IF NOT EXISTS auth_service_db;
CREATE DATABASE IF NOT EXISTS account_service_db;
CREATE DATABASE IF NOT EXISTS transaction_service_db;
CREATE DATABASE IF NOT EXISTS notification_service_db;

GRANT ALL PRIVILEGES ON auth_service_db.* TO 'banking_app'@'%';
GRANT ALL PRIVILEGES ON account_service_db.* TO 'banking_app'@'%';
GRANT ALL PRIVILEGES ON transaction_service_db.* TO 'banking_app'@'%';
GRANT ALL PRIVILEGES ON notification_service_db.* TO 'banking_app'@'%';

FLUSH PRIVILEGES;
