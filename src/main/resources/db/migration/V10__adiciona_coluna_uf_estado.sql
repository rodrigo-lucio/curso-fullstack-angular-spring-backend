ALTER TABLE estado ADD COLUMN uf VARCHAR(2) NOT NULL DEFAULT '';

UPDATE estado SET uf = 'AC' where codigo = 1;
UPDATE estado SET uf = 'AL' where codigo = 2;
UPDATE estado SET uf = 'AP' where codigo = 4;
UPDATE estado SET uf = 'AM' where codigo = 3;
UPDATE estado SET uf = 'BA' where codigo = 5;
UPDATE estado SET uf = 'CE' where codigo = 6;
UPDATE estado SET uf = 'DF' where codigo = 7;
UPDATE estado SET uf = 'ES' where codigo = 8;
UPDATE estado SET uf = 'GO' where codigo = 9;
UPDATE estado SET uf = 'MA' where codigo = 10;
UPDATE estado SET uf = 'MG' where codigo = 11;
UPDATE estado SET uf = 'MS' where codigo = 12;
UPDATE estado SET uf = 'MT' where codigo = 13;
UPDATE estado SET uf = 'PA' where codigo = 14;
UPDATE estado SET uf = 'PB' where codigo = 15;
UPDATE estado SET uf = 'PR' where codigo = 18;
UPDATE estado SET uf = 'PE' where codigo = 16;
UPDATE estado SET uf = 'PI' where codigo = 17;
UPDATE estado SET uf = 'RJ' where codigo = 19;
UPDATE estado SET uf = 'RN' where codigo = 20;
UPDATE estado SET uf = 'RS' where codigo = 23;
UPDATE estado SET uf = 'RO' where codigo = 21;
UPDATE estado SET uf = 'RR' where codigo = 22;
UPDATE estado SET uf = 'SC' where codigo = 24;
UPDATE estado SET uf = 'SE' where codigo = 25;
UPDATE estado SET uf = 'SP' where codigo = 26;
UPDATE estado SET uf = 'TO' where codigo = 27;