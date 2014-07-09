package usbong.android.builder.models;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
@Table(name = "Utrees", id = BaseColumns._ID)
public class Utree extends Model implements BaseColumns, Serializable {

    @Column(name = "Name")
    public String name;

    public List<Screen> screens() {
        return getMany(Screen.class, "Utree");
    }

}
