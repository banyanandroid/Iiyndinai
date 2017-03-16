package com.banyan.iiyndinai.activity;

import java.util.ArrayList;

/**
 * Created by Jo on 8/12/2016.
 */
public class AppConfig {

    static String url_login = "http://iiyndinai.com/android/user_login.php";
    static String url_register = "http://iiyndinai.com/android/user_registration.php";
    static String url_products = "http://iiyndinai.com/android/products.php";
    static String url_product_description = "http://iiyndinai.com/android/description.php";
    static String url_product_add_wishlist = "http://iiyndinai.com/android/add_wishlist.php";
    static String url_product_wishlist_list = "http://iiyndinai.com/android/showwishlist.php";
    static String url_product_wishlist_remove = "http://iiyndinai.com/android/wishlist_remove.php";
    static String url_product_send_enquiry = "http://iiyndinai.com/android/enquiry.php";
    static String url_product_add_to_cart = "http://iiyndinai.com/android/add_cart.php";
    static String url_cart_count = "http://iiyndinai.com/android/product_count.php";
    static String url_cart_showlist = "http://iiyndinai.com/android/show_cart.php";
    static String url_cart_Remove_Cart_item = "http://iiyndinai.com/android/remove_cart.php";
    static String url_delivery_Status = "http://iiyndinai.com/android/pincode_check.php";
    static String url_search_prod_list = "http://iiyndinai.com/android/products_list.php";
    static String url_search_prod = "http://iiyndinai.com/android/search_products.php";

    static String url_payment = "http://iiyndinai.com/android/payment.php";
    static String url_payment_success = "http://iiyndinai.com/android/payment_success.php";
    static String url_payment_fail = "http://iiyndinai.com/android/payment_faild.php";
    static String url_myorder = "http://iiyndinai.com/android/app_order_history.php";

    static String url_status_offline = "http://iiyndinai.com/android/offline_payment.php";

    static String url_edit_myaccount = "http://iiyndinai.com/android/update_account.php";
    static String url_update_myaccount = "http://iiyndinai.com/android/update_user.php";
    static String url_update_cart = "http://iiyndinai.com/android/update_cart.php";

    static String url_order_list = "http://iiyndinai.com/android/list_order_history.php";
    static String url_description_add_to_cart = "http://iiyndinai.com/android/view_products_add_cart.php";

    static String cid = null;
    static String aid = null;
    static String cart_val = null;
    static String order_id = null;

    static ArrayList cart_id_arr = new ArrayList();
    static ArrayList cart_name_arr = new ArrayList();
    static ArrayList cart_weight_arr = new ArrayList();
    static ArrayList cart_price_arr = new ArrayList();
    static ArrayList cart_image_arr = new ArrayList();

}
