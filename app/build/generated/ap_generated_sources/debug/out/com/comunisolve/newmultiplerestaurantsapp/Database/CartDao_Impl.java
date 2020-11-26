package com.comunisolve.newmultiplerestaurantsapp.Database;

import android.database.Cursor;
import androidx.room.EmptyResultSetException;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.RxRoom;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CartDao_Impl implements CartDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CartItem> __insertionAdapterOfCartItem;

  private final EntityDeletionOrUpdateAdapter<CartItem> __deletionAdapterOfCartItem;

  private final EntityDeletionOrUpdateAdapter<CartItem> __updateAdapterOfCartItem;

  private final SharedSQLiteStatement __preparedStmtOfCleanCart;

  public CartDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCartItem = new EntityInsertionAdapter<CartItem>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `Cart` (`foodId`,`foodName`,`foodImage`,`foodPrice`,`foodQuantity`,`userPhone`,`restaurantId`,`foodAddon`,`foodSize`,`foodExtraPrice`,`fbid`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartItem value) {
        stmt.bindLong(1, value.getFoodId());
        if (value.getFoodName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getFoodName());
        }
        if (value.getFoodImage() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFoodImage());
        }
        if (value.getFoodPrice() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindDouble(4, value.getFoodPrice());
        }
        stmt.bindLong(5, value.getFoodQuantity());
        if (value.getUserPhone() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getUserPhone());
        }
        stmt.bindLong(7, value.getRestaurantId());
        if (value.getFoodAddon() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getFoodAddon());
        }
        if (value.getFoodSize() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getFoodSize());
        }
        if (value.getFoodExtraPrice() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindDouble(10, value.getFoodExtraPrice());
        }
        if (value.getFbid() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getFbid());
        }
      }
    };
    this.__deletionAdapterOfCartItem = new EntityDeletionOrUpdateAdapter<CartItem>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Cart` WHERE `foodId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartItem value) {
        stmt.bindLong(1, value.getFoodId());
      }
    };
    this.__updateAdapterOfCartItem = new EntityDeletionOrUpdateAdapter<CartItem>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR REPLACE `Cart` SET `foodId` = ?,`foodName` = ?,`foodImage` = ?,`foodPrice` = ?,`foodQuantity` = ?,`userPhone` = ?,`restaurantId` = ?,`foodAddon` = ?,`foodSize` = ?,`foodExtraPrice` = ?,`fbid` = ? WHERE `foodId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartItem value) {
        stmt.bindLong(1, value.getFoodId());
        if (value.getFoodName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getFoodName());
        }
        if (value.getFoodImage() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFoodImage());
        }
        if (value.getFoodPrice() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindDouble(4, value.getFoodPrice());
        }
        stmt.bindLong(5, value.getFoodQuantity());
        if (value.getUserPhone() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getUserPhone());
        }
        stmt.bindLong(7, value.getRestaurantId());
        if (value.getFoodAddon() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getFoodAddon());
        }
        if (value.getFoodSize() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getFoodSize());
        }
        if (value.getFoodExtraPrice() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindDouble(10, value.getFoodExtraPrice());
        }
        if (value.getFbid() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getFbid());
        }
        stmt.bindLong(12, value.getFoodId());
      }
    };
    this.__preparedStmtOfCleanCart = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Cart WHERE fbid=? AND restaurantId=?";
        return _query;
      }
    };
  }

  @Override
  public Completable insertOrReplaceAll(final CartItem... cartItems) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCartItem.insert(cartItems);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Single<Integer> deleteCart(final CartItem cart) {
    return Single.fromCallable(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total +=__deletionAdapterOfCartItem.handle(cart);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Single<Integer> updateCart(final CartItem cartItem) {
    return Single.fromCallable(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total +=__updateAdapterOfCartItem.handle(cartItem);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Single<Integer> cleanCart(final String fbid, final int restaurantId) {
    return Single.fromCallable(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCleanCart.acquire();
        int _argIndex = 1;
        if (fbid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, fbid);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, restaurantId);
        __db.beginTransaction();
        try {
          final java.lang.Integer _result = _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
          __preparedStmtOfCleanCart.release(_stmt);
        }
      }
    });
  }

  @Override
  public Flowable<List<CartItem>> getAllCart(final String fbid, final int restaurantId) {
    final String _sql = "SELECT * FROM Cart WHERE fbid=? AND restaurantId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (fbid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fbid);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, restaurantId);
    return RxRoom.createFlowable(__db, false, new String[]{"Cart"}, new Callable<List<CartItem>>() {
      @Override
      public List<CartItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfFoodImage = CursorUtil.getColumnIndexOrThrow(_cursor, "foodImage");
          final int _cursorIndexOfFoodPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "foodPrice");
          final int _cursorIndexOfFoodQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "foodQuantity");
          final int _cursorIndexOfUserPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhone");
          final int _cursorIndexOfRestaurantId = CursorUtil.getColumnIndexOrThrow(_cursor, "restaurantId");
          final int _cursorIndexOfFoodAddon = CursorUtil.getColumnIndexOrThrow(_cursor, "foodAddon");
          final int _cursorIndexOfFoodSize = CursorUtil.getColumnIndexOrThrow(_cursor, "foodSize");
          final int _cursorIndexOfFoodExtraPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "foodExtraPrice");
          final int _cursorIndexOfFbid = CursorUtil.getColumnIndexOrThrow(_cursor, "fbid");
          final List<CartItem> _result = new ArrayList<CartItem>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final CartItem _item;
            _item = new CartItem();
            final int _tmpFoodId;
            _tmpFoodId = _cursor.getInt(_cursorIndexOfFoodId);
            _item.setFoodId(_tmpFoodId);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            _item.setFoodName(_tmpFoodName);
            final String _tmpFoodImage;
            _tmpFoodImage = _cursor.getString(_cursorIndexOfFoodImage);
            _item.setFoodImage(_tmpFoodImage);
            final Double _tmpFoodPrice;
            if (_cursor.isNull(_cursorIndexOfFoodPrice)) {
              _tmpFoodPrice = null;
            } else {
              _tmpFoodPrice = _cursor.getDouble(_cursorIndexOfFoodPrice);
            }
            _item.setFoodPrice(_tmpFoodPrice);
            final int _tmpFoodQuantity;
            _tmpFoodQuantity = _cursor.getInt(_cursorIndexOfFoodQuantity);
            _item.setFoodQuantity(_tmpFoodQuantity);
            final String _tmpUserPhone;
            _tmpUserPhone = _cursor.getString(_cursorIndexOfUserPhone);
            _item.setUserPhone(_tmpUserPhone);
            final int _tmpRestaurantId;
            _tmpRestaurantId = _cursor.getInt(_cursorIndexOfRestaurantId);
            _item.setRestaurantId(_tmpRestaurantId);
            final String _tmpFoodAddon;
            _tmpFoodAddon = _cursor.getString(_cursorIndexOfFoodAddon);
            _item.setFoodAddon(_tmpFoodAddon);
            final String _tmpFoodSize;
            _tmpFoodSize = _cursor.getString(_cursorIndexOfFoodSize);
            _item.setFoodSize(_tmpFoodSize);
            final Double _tmpFoodExtraPrice;
            if (_cursor.isNull(_cursorIndexOfFoodExtraPrice)) {
              _tmpFoodExtraPrice = null;
            } else {
              _tmpFoodExtraPrice = _cursor.getDouble(_cursorIndexOfFoodExtraPrice);
            }
            _item.setFoodExtraPrice(_tmpFoodExtraPrice);
            final String _tmpFbid;
            _tmpFbid = _cursor.getString(_cursorIndexOfFbid);
            _item.setFbid(_tmpFbid);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Single<Integer> countItemInCart(final String fbid, final int restaurantId) {
    final String _sql = "SELECT COUNT(*) FROM Cart WHERE fbid=? AND restaurantId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (fbid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fbid);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, restaurantId);
    return RxRoom.createSingle(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if(_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          if(_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Single<Long> sumPrice(final String fbid, final int restaurantId) {
    final String _sql = "SELECT SUM(foodPrice*foodQuantity)+(foodExtraPrice*foodQuantity) FROM Cart WHERE fbid=? AND restaurantId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (fbid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fbid);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, restaurantId);
    return RxRoom.createSingle(new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if(_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          if(_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Single<CartItem> getItemInCart(final String foodId, final String fbid,
      final int restaurantId) {
    final String _sql = "SELECT * FROM Cart WHERE foodId=? AND fbid=? AND restaurantId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (foodId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, foodId);
    }
    _argIndex = 2;
    if (fbid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fbid);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, restaurantId);
    return RxRoom.createSingle(new Callable<CartItem>() {
      @Override
      public CartItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfFoodImage = CursorUtil.getColumnIndexOrThrow(_cursor, "foodImage");
          final int _cursorIndexOfFoodPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "foodPrice");
          final int _cursorIndexOfFoodQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "foodQuantity");
          final int _cursorIndexOfUserPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhone");
          final int _cursorIndexOfRestaurantId = CursorUtil.getColumnIndexOrThrow(_cursor, "restaurantId");
          final int _cursorIndexOfFoodAddon = CursorUtil.getColumnIndexOrThrow(_cursor, "foodAddon");
          final int _cursorIndexOfFoodSize = CursorUtil.getColumnIndexOrThrow(_cursor, "foodSize");
          final int _cursorIndexOfFoodExtraPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "foodExtraPrice");
          final int _cursorIndexOfFbid = CursorUtil.getColumnIndexOrThrow(_cursor, "fbid");
          final CartItem _result;
          if(_cursor.moveToFirst()) {
            _result = new CartItem();
            final int _tmpFoodId;
            _tmpFoodId = _cursor.getInt(_cursorIndexOfFoodId);
            _result.setFoodId(_tmpFoodId);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            _result.setFoodName(_tmpFoodName);
            final String _tmpFoodImage;
            _tmpFoodImage = _cursor.getString(_cursorIndexOfFoodImage);
            _result.setFoodImage(_tmpFoodImage);
            final Double _tmpFoodPrice;
            if (_cursor.isNull(_cursorIndexOfFoodPrice)) {
              _tmpFoodPrice = null;
            } else {
              _tmpFoodPrice = _cursor.getDouble(_cursorIndexOfFoodPrice);
            }
            _result.setFoodPrice(_tmpFoodPrice);
            final int _tmpFoodQuantity;
            _tmpFoodQuantity = _cursor.getInt(_cursorIndexOfFoodQuantity);
            _result.setFoodQuantity(_tmpFoodQuantity);
            final String _tmpUserPhone;
            _tmpUserPhone = _cursor.getString(_cursorIndexOfUserPhone);
            _result.setUserPhone(_tmpUserPhone);
            final int _tmpRestaurantId;
            _tmpRestaurantId = _cursor.getInt(_cursorIndexOfRestaurantId);
            _result.setRestaurantId(_tmpRestaurantId);
            final String _tmpFoodAddon;
            _tmpFoodAddon = _cursor.getString(_cursorIndexOfFoodAddon);
            _result.setFoodAddon(_tmpFoodAddon);
            final String _tmpFoodSize;
            _tmpFoodSize = _cursor.getString(_cursorIndexOfFoodSize);
            _result.setFoodSize(_tmpFoodSize);
            final Double _tmpFoodExtraPrice;
            if (_cursor.isNull(_cursorIndexOfFoodExtraPrice)) {
              _tmpFoodExtraPrice = null;
            } else {
              _tmpFoodExtraPrice = _cursor.getDouble(_cursorIndexOfFoodExtraPrice);
            }
            _result.setFoodExtraPrice(_tmpFoodExtraPrice);
            final String _tmpFbid;
            _tmpFbid = _cursor.getString(_cursorIndexOfFbid);
            _result.setFbid(_tmpFbid);
          } else {
            _result = null;
          }
          if(_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
