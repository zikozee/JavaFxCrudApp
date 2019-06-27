package sample;

import sample.Model.Customer;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class dynamicReports {

    private List<Customer> candidates;

    public dynamicReports(List<Customer> derivedCandidates) {
        this.candidates = derivedCandidates;
        build();
    }

    private void build() {
        StyleBuilder boldStyle = stl.style().bold();

        StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)

                .setBorder(stl.pen1Point())

                .setBackgroundColor(Color.LIGHT_GRAY);


        // title, field name data type

        CurrencyType currencyType = new CurrencyType();

        TextColumnBuilder<String> clientName = col.column("Client Name", "client_name", type.stringType())
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> pickUpAddress = col.column("Pickup Address", "pickup_address", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> clientPhone = col.column("Client Phone", "client_phone", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> date = col.column("Date", "date", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> contactPerson = col.column("Contact Person", "contact_person", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> deliveryAddress = col.column("Delivery Address", "delivery_address", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> contactPhone = col.column("Contact Phone", "contact_phone", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> staff = col.column("Staff", "staff", type.stringType()).setStyle(boldStyle)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<BigDecimal> charge = col.column("Charge", "charge", currencyType)
                .setStyle(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);


        // price = unitPrice * quantity

        //TextColumnBuilder<BigDecimal> priceColumn = unitPriceColumn.multiply(quantityColumn).setTitle("Price");
        // PercentageColumnBuilder pricePercColumn = col.percentageColumn("Price %", priceColumn);
        TextColumnBuilder<Integer> rowNumberColumn = col.reportRowNumberColumn("No.")
                //*************sets the fixed width of a column, width = 2 * character width***********************************
                //n order to show all characters
                .setFixedColumns(2)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        try {

            report()// create new report design
                    .setColumnTitleStyle(columnTitleStyle)
                    .setSubtotalStyle(boldStyle)
                    .highlightDetailEvenRows()
                    .columns(// add columns
                            rowNumberColumn, clientName, pickUpAddress, clientPhone, date, contactPerson,
                            deliveryAddress, contactPhone, staff, charge)
                    .groupBy(clientName)
                    .subtotalsAtSummary(
                            sbt.sum(charge)) // base summary
                    .subtotalsAtFirstGroupFooter(
                            sbt.sum(charge))// category summary
                    .title(cmp.text("MUVE LOGISTICS").setStyle(boldCenteredStyle))// shows report title
                    .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))// shows number of page at page footer
                    .setDataSource(createDataSource())// set datasource
                    .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE) // to set to landscape for large data
                    .show(false);// create and show report


        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JRDataSource createDataSource() {
        DRDataSource dataSource = new DRDataSource("client_name", "pickup_address", "client_phone", "date",
                "contact_person", "delivery_address", "contact_phone", "staff", "charge");

        // no need to add Cash_Status
        for (Customer customer : candidates) {
            dataSource.add(customer.getClient_name(), customer.getPickup_address(), customer.getClient_phone(),
                    customer.getDate().toString(), customer.getContact_person(), customer.getDelivery_address(),
                    customer.getContact_phone(), customer.getStaff(), new BigDecimal(customer.getCharge()));
        }

        return dataSource;
    }
}

class CurrencyType extends BigDecimalType {
    private static final long serialVersionUID = 1L;

    @Override
    public String getPattern() {
        return "=N= #,###.00";
    }
}
