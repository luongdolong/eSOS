package com.xyz.automate.esos.logic.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by luongdolong on 9/12/2017.
 */
@Table(name = "CallData")
public class CallDataModel extends Model {
    @Column(name = "type")
    public int type; //out in
    @Column(name = "kind")
    public int kind; //sos, phone
    @Column(name = "sender")
    public String sender;
    @Column(name = "agent")
    public int agent;
    @Column(name = "unitName")
    public String unitName;
    @Column(name = "userName")
    public String userName;
    @Column(name = "tel")
    public String tel;
    @Column(name = "time")
    public Date time;
}
