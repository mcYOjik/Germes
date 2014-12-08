package com.l_0k.germes;

import android.content.Context;
import android.content.res.Resources;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by knyazev_o on 28.10.2014.
 */

public class Task {
    Context context;
    //DB fields
    private int _id;
    private String task1cID;
    private String createDate;
    private String upToDate;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String shippingWarehouse;
    private int status;
    private ArrayList<GoodsList> goodsList;
    //Synthetic fields
    private String statusText;
    private String goods;

    Task () {
        goodsList = new ArrayList<GoodsList>();
    }

    public class GoodsList {
        private String goods;
        private int quantity;
    }

    public static final int STATUS_DELIVERING = 0;
    public static final int STATUS_DRIVER_REFUSED = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_CUSTOMER_REFUSED = 3;

    Task (Context _context, int __id, String _task1cID, String _createDate, String _upToDate,
          String _customerName ,String _customerPhone, String _customerAddress,
          String _shippingWarehouse, int _status, String _goods) {

        context = _context;

        _id = __id;
        task1cID = _task1cID;
        createDate= _createDate;
        upToDate = _upToDate;
        customerName = _customerName;
        customerPhone = _customerPhone;
        customerAddress = _customerAddress;
        shippingWarehouse = _shippingWarehouse;
        setStatus(_status);
        goods = _goods;

        goodsList = new ArrayList<GoodsList>();
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTask1cID() {
        return task1cID;
    }

    public void setTask1cID(String task1cID) {
        this.task1cID = task1cID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpToDate() {
        return upToDate;
    }

    public void setUpToDate(String upToDate) {
        this.upToDate = upToDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getShippingWarehouse() {
        return shippingWarehouse;
    }

    public void setShippingWarehouse(String shippingWarehouse) {
        this.shippingWarehouse = shippingWarehouse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        switch (status) {
            case STATUS_DELIVERING:
                statusText = context.getString(R.string.Delivering);
                break;
            case STATUS_DRIVER_REFUSED:
                statusText = context.getString(R.string.DriverRefused);
                break;
            case STATUS_DELIVERED:
                statusText = context.getString(R.string.Delivered);
                break;
            case STATUS_CUSTOMER_REFUSED:
                statusText = context.getString(R.string.CustomerRefused);
                break;
            default:
                statusText = context.getString(R.string.Delivering);
        }
    }

    public String getStatusText() {
        return statusText;
    }

    public String getGoods() {
        return goods;
    }

    public void addGoods(String goods,int quantity){
        GoodsList goodsListAdd = new GoodsList();
        goodsListAdd.goods = goods;
        goodsListAdd.quantity = quantity;

        goodsList.add(goodsListAdd);
        this.goods += goods + " - " + Integer.toString(quantity) + "\n";
    }

//    public ArrayList<GoodsList> getGoodsList(){
//        return goodsList;
//    }

    public int getGoodsListItemsCount() {
        return goodsList.size();
    }

    public String getGoodsListGoods(int index){
        return goodsList.get(index).goods;
    }

    public int getGoodsListQuantity(int index){
        return goodsList.get(index).quantity;
    }
}