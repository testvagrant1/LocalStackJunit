import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.List;

public class LocalStackDynamoDB {

    private String disableSSLVerification = "false";
    private DynamoDB client;

    public void init(AmazonDynamoDB client){
        this.client = new DynamoDB(client);
    }

    public boolean isDisableSSLVerification() {
        return Boolean.parseBoolean(disableSSLVerification);
    }

    public void setDisableSSLVerification(boolean disableSSLVerification) {
        this.disableSSLVerification = String.format("%b",disableSSLVerification);
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, this.disableSSLVerification);
    }

    public LocalStackDynamoDB createTable(String tableName, List<KeySchemaElement> keySchemaList,
                            List<AttributeDefinition> attributeDefinitionsList,
                            ProvisionedThroughput provisionedThroughput){
        try {
            Table table = this.client.createTable(tableName, keySchemaList, attributeDefinitionsList, provisionedThroughput);
            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
        return this;
    }

    public LocalStackDynamoDB deleteTable(String tableName){
        try {
            this.client.getTable(tableName).delete();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return this;
    }

    public Table getTable(String tableName){
        try {
            for(Table table : this.getTables()){
                if(table.getTableName().equals(tableName)){
                    return table;
                }
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    public DynamoDB getClient(){
        return this.client;
    }

    public TableCollection<ListTablesResult> getTables(){
        return this.client.listTables();
    }

}
