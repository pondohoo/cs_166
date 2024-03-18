DROP TRIGGER IF EXISTS updateunits ON orders;

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
