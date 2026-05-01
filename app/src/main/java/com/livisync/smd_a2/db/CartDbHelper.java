package com.livisync.smd_a2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.livisync.smd_a2.models.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper for managing shopping cart using SQLite.
 * Handles CRUD operations for the cart table.
 */
public class CartDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fastmart_cart.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CART = "cart";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_IMAGE_RES_ID = "imageResId";

    // SQL to create table
    private static final String CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT_ID + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1, " +
                    COLUMN_IMAGE_RES_ID + " INTEGER)";

    public CartDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    /**
     * Add an item to the cart.
     * @param cartItem CartItem object to add
     * @return true if insertion successful, false otherwise
     */
    public boolean addToCart(CartItem cartItem) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_ID, cartItem.getProductId());
            values.put(COLUMN_NAME, cartItem.getName());
            values.put(COLUMN_TYPE, cartItem.getType());
            values.put(COLUMN_PRICE, cartItem.getPrice());
            values.put(COLUMN_QUANTITY, cartItem.getQuantity());
            values.put(COLUMN_IMAGE_RES_ID, cartItem.getImageResId());

            long result = db.insert(TABLE_CART, null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update quantity of a product in cart.
     * @param productId Product ID
     * @param newQuantity New quantity value
     * @return true if update successful, false otherwise
     */
    public boolean updateQuantity(String productId, int newQuantity) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQuantity);

            int result = db.update(TABLE_CART, values, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove an item from the cart.
     * @param productId Product ID to remove
     * @return true if deletion successful, false otherwise
     */
    public boolean removeFromCart(String productId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete(TABLE_CART, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a product exists in cart.
     * @param productId Product ID to check
     * @return true if product is in cart, false otherwise
     */
    public boolean isInCart(String productId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_CART, null, COLUMN_PRODUCT_ID + " = ?",
                    new String[]{productId}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all items in the cart.
     * @return List of CartItem objects
     */
    public List<CartItem> getAllCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_CART, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String productId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID));

                    CartItem item = new CartItem(productId, name, type, price, imageResId, quantity);
                    item.setId(id);
                    cartItems.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    /**
     * Calculate the total price of all items in cart.
     * @return Total price
     */
    public double getTotalPrice() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_PRICE + " * " + COLUMN_QUANTITY + ") FROM " + TABLE_CART, null);
            cursor.moveToFirst();
            double total = cursor.getDouble(0);
            cursor.close();
            db.close();
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Get the count of items in cart.
     * @return Number of items in cart
     */
    public int getCartItemCount() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CART, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Clear all items from the cart.
     */
    public void clearCart() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CART, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a specific cart item by product ID.
     * @param productId Product ID
     * @return CartItem object or null if not found
     */
    public CartItem getCartItem(String productId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_CART, null, COLUMN_PRODUCT_ID + " = ?",
                    new String[]{productId}, null, null, null);

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID));

                CartItem item = new CartItem(productId, name, type, price, imageResId, quantity);
                item.setId(id);
                cursor.close();
                db.close();
                return item;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
