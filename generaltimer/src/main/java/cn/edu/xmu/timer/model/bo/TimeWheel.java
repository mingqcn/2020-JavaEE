package cn.edu.xmu.timer.model.bo;


import java.util.*;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 21:20
 **/
public class TimeWheel {
    /**
     * 当前位置
     */
    private int current = 0;

    private int num = 0;

    private Vector<List<Task>> cells = null;

    public TimeWheel(int num, int current){
        this.cells = new Vector<>(num);
        this.num = num;
        this.current = current;

        for (int i = 0; i< num; i++){
            this.cells.add(new ArrayList<>(1));
        }
    }

    /**
     * 获得下一时刻任务
     * @return
     */
    public synchronized List<Task> nextCell(){
        int index = current;
        this.current = (this.current + 1) % this.num;
        return this.cells.get(index);
    }

    /**
     * 装载下轮任务
     * @param taskMap key是装载的位置， value是装载的任务list
     */
    public synchronized void loadNextRound(Map<Integer, List<Task>> taskMap){
        int beginIndex = 0;
        int endIndex = this.num/2;
        if (current <= this.num /2){
            beginIndex = this.num / 2;
            endIndex = this.num;
        }

        for(int i = beginIndex; i < endIndex; i++){
            List<Task> taskList = taskMap.get(Integer.valueOf(i % (this.num / 2)));
            if (null != taskList) {
                this.cells.set(i, taskList);
            }
        }
    }

    public int getNum() {
        return num;
    }

    public int getCurrent() {
        return current;
    }

    public Vector<List<Task>> getCells() {
        return cells;
    }

    /**
     * 从当前时间轮中移除任务
     * @param removeIds 待清除的任务id
     */
    public synchronized void removeTask(List<Long> removeIds) {
        for (List<Task> tasks : this.cells) {
            if (null != tasks) {
                // 通过每一个 task 的 id 对 removeIds 进行筛选
                // 若筛选结果个数多于 0L 则把该 task 移除
                tasks   .removeIf(taskItem -> removeIds
                        .stream()
                        .filter(removeId -> taskItem.getId().equals(removeId))
                        .count() > 0L);
            }
        }
    }

}
