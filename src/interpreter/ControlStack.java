package interpreter;

import java.util.ArrayList;

/**
 * 运行时刻栈，用于过程调用以及指令计算。
 */
public class ControlStack {

    /** 运行时刻栈，用ArrayList抽象表示，运行行时刻栈最大值为4KB*/
    private ArrayList<Integer> stack = new ArrayList<>(1000);
    /** 全局display表，最大嵌套深度限制100层 */
    private ArrayList<Integer> display = new ArrayList<>(100);
    /** 帧指针,指向当前活动记录基地址 */
    private int sp = 0;
    /** 栈指针，指向当前活动记录顶层地址 */
    private int top = 0;

    public ControlStack() {}

    public void callProcedure(int ret, int para_num, ArrayList<Integer> paras) {
        var act_record =  new ActivationRecord(ret, sp, display.get(0), para_num, paras);

    }

    public void retProcedure(){
        // 恢复top指针
        top = sp - 1;
        // 恢复帧指针
        sp = stack.get(sp + 1);

    }

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
        /** 参数个数 */
        private int para_num;
        /** 参数值 */
        private ArrayList<Integer> paras;

        /**
         * @param ret_address 返回地址，即调用者活动记录基地址
         * @param sp_link 动态链，即旧的sp地址
         * @param display_link 保存的显示表值
         * @param para_num 参数个数
         * @param paras 参数值
         */
        public ActivationRecord(int ret_address, int sp_link, int display_link, int para_num, ArrayList<Integer> paras) {
            this.ret_address = ret_address;
            this.sp_link = sp_link;
            this.display_link = display_link;
            this.para_num = para_num;
            this.paras = paras;
        }
    }

}
