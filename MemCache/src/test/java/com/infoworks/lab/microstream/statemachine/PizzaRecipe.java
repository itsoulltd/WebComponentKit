package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.statemachine.pizzas.ThickCrustPizza;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@javax.persistence.Entity(name = "PIZZA_RECIPE")
@TableName(value = "PIZZA_RECIPE", acceptAll = false)
public class PizzaRecipe extends Entity {

    @Id
    @PrimaryKey(name="RECIPE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RECIPE_ID", length = 100)
    private String recipeId;

    @Column(name = "PRICE")
    private String price;

    @Column(name = "NAME")
    private String className;

    @Column(name = "DESCRIPTION")
    private String description;

    public PizzaRecipe() {
        this(UUID.randomUUID().toString(), "1.00", ThickCrustPizza.class.getName());
    }

    public PizzaRecipe(String recipeId, String price, String className) {
        this(recipeId, price, className, "");
    }

    public PizzaRecipe(String recipeId, String price, String className, String description) {
        this.recipeId = recipeId;
        this.price = price;
        this.className = className;
        this.description = description;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
