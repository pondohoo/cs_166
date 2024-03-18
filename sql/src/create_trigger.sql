DROP TRIGGER IF EXISTS updateunits ON orders;
DROP TRIGGER IF EXISTS updatesupply ON ProductSupplyRequests;

CREATE OR REPLACE FUNCTION update_product_units()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Product 
    SET numberOfUnits = numberOfUnits - NEW.unitsordered 
    WHERE storeID = NEW.storeid AND productName = NEW.productName;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER updateunits 
AFTER INSERT ON orders 
FOR EACH ROW
EXECUTE PROCEDURE update_product_units();

CREATE OR REPLACE FUNCTION update_supply_request()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Product 
    SET numberOfUnits = numberOfUnits + NEW.unitsRequested 
    WHERE storeID = NEW.storeid AND productName = NEW.productName;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER updatesupply 
AFTER INSERT ON ProductSupplyRequests 
FOR EACH ROW
EXECUTE PROCEDURE update_supply_request();



