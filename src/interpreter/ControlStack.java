package interpreter;

import java.util.ArrayList;

/**
 * 运行时刻栈，用于过程调用以及指令计算。
 */
public class ControlStack {

    /** 运行时刻栈，用ArrayList抽象表示*/
    ArrayList<Integer> stack = new ArrayList<>();
    /** 全局display表，最大嵌套深度限制100层 */
    ArrayList<Integer> display = new ArrayList<>(100);


    public ControlStack() {}

    /**
     * ControlStack内部类
     * 活动记录类，用于生成一项新的活动记录
     */
    public class ActivationRecord {
        /** 返回地址，即调用者活动记录基地址 */
        private int ret_address;
        /** 动态链，即旧的sp地址 */
        private int sp_link;
        /** 保存的显示表值 */
        private int display_link;
        /** 参数值 */
        private ArrayList<Integer> paras;

        /**
         * @param ret_address 返回地址，即调用者活动记录基地址
         * @param sp_link 动态链，即旧的sp地址
         * @param display_link 保存的显示表值
         * @param paras 参数值
         */
        public ActivationRecord(int ret_address, int sp_link, int display_link, ArrayList<Integer> paras) {
            this.ret_address = ret_address;
            this.sp_link = sp_link;
            this.display_link = display_link;
            this.paras = paras;
        }
    }
}
