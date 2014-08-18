package usbong.android.builder.models;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/30/2014.
 */
@Table(name = "ScreenRelations", id = BaseColumns._ID)
public class ScreenRelation extends Model implements BaseColumns, Serializable {

    @Column(name = "parent", onDelete = Column.ForeignKeyAction.CASCADE)
    public Screen parent;
    @Column(name = "child", onDelete = Column.ForeignKeyAction.CASCADE)
    public Screen child;
    @Column(name = "condition")
    public String condition;

    public static int deleteAll(long screenId) {
        int count = new Select().from(ScreenRelation.class).where("parent = ?", screenId).count();
        new Delete().from(ScreenRelation.class).where("parent = ?", screenId).execute();
        return count;
    }

    public static List<ScreenRelation> getParentsOf(long screenId) {
        return new Select().from(ScreenRelation.class).where("child = ?", screenId).execute();
    }

    public static List<ScreenRelation> getChildrenOf(long screenId) {
        return new Select().from(ScreenRelation.class).where("parent = ?", screenId).execute();
    }
}
