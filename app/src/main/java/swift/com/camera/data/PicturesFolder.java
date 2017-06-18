package swift.com.camera.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bool on 17-6-16.
 *
 * 原来只获取了图片路径，随着需求改变，需要按照图片文件夹归类图片，还需要添加日期等信息。
 * 为了能够更好的组织数据，这就需要重新设计数据结构。将相同文件夹下的图片放到一个ArrayList
 * 中。在使用文件夹名作为key，ArrayList作为值存放到HashMap中。由于需求的改变需要重新设计
 * 数据结构，而引起的混乱是糟糕的。为此需要修改每一处使用原有数据的地方。为了防止以后再次出现
 * 数据结构的改变，我决定对数据结构进行封装。
 * 1，然而如果封装成类的组件，每次访问数据需要多调用一层函数，即便每次调用很少，然而对数据的
 *    访问是频繁的。所以想到了继承的方式。
 * 2，封装类名不建议使用明显带有数据结构特征的名字，因为数据结构可能改变，改变后的不修改名字会
 *   误导别人。改名字会有大量的修改工作。
 */

public class PicturesFolder extends HashMap<String, List> {
    // 数据封装似乎是把双刃剑，不封装函数，难以修改；封装函数，降低效率。
    public boolean haveFolder(String folderName) {
        return this.containsKey(folderName);
    }

    public boolean add(PictureInfo pictureInfo) {
        File imageFile = new File(pictureInfo.getImagePath());
        if( imageFile.exists()) {
            String filderName = imageFile.getParent(); // 使用文件夹名作为Map的key
            List<PictureInfo> pictureInfoList;
            if ( this.containsKey(filderName)) {
                pictureInfoList = (ArrayList)this.get(filderName);
            } else {
                pictureInfoList = new ArrayList<PictureInfo>();
                this.put(filderName, pictureInfoList);
            }
            pictureInfoList.add(pictureInfo);
            return true;
        } else {
            return false;
        }
    }
}
