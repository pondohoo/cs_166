DROP INDEX IF EXISTS idx_store_managerID;
DROP INDEX IF EXISTS idx_product_storeID;
DROP INDEX IF EXISTS idx_orders_customerID;
DROP INDEX IF EXISTS idx_supply_requests_managerID;
DROP INDEX IF EXISTS idx_product_updates_managerID;

CREATE INDEX idx_store_managerID ON Store(managerID);
CREATE INDEX idx_product_storeID ON Product(storeID);
CREATE INDEX idx_orders_customerID ON Orders(customerID);
CREATE INDEX idx_supply_requests_managerID ON ProductSupplyRequests(managerID);
CREATE INDEX idx_product_updates_managerID ON ProductUpdates(managerID);