# OdooAPI4J
Odoo API helper for java (i love RoR,Laravel...)

## Usage

1. Extend **BaseModel**

```java
@Table(value = "res.partner",primaryKey = "id")
class Partner extend BaseModel {
    private int id;
    private String ref;
    private String partner_code;
    private String name;
    private String street;
    private String street2;
    private String comment;
    private String contacts;
    private String contacts_phone;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

2. Authenticate (APIHelper.Authenticate)

3. Use It

```java
  Partner p = new Partner().Find(1); // Find Partner by id
  
  p.setName("Hex");
  p.Create(); // Create partner
  
  p.setName("HexPang");
  p.Update(); // Update
```

### Config in **APIHelper.java**
```java
    private final String HOST_KEY = System.getProperty("HOST_NAME");
    private final int PORT_KEY = Integer.valueOf(System.getProperty("PORT_NUM"));
    private final String DATABASENAME_KEY = System.getProperty("DB_NAME");
```
