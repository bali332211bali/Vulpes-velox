package com.vulpes.velox.models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "bulk_products")
public class BulkProduct extends Product  {

  @OneToMany(mappedBy = "bulkProduct", cascade = CascadeType.REMOVE)
  private List<Shipment> shipments;

  public List<Shipment> getShipments() {
    return shipments;
  }

  public void setShipments(List<Shipment> shipments) {
    this.shipments = shipments;
  }
}