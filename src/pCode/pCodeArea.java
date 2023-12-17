package pCode;

import java.util.ArrayList;

public class pCodeArea {
    /**
     * 静态代码区，储存类pCode代码
     */
    public ArrayList<pCode> codes = new ArrayList<>();

    /**
     * 无参构造函数
     */
    public pCodeArea() {}

    /**
     * 生成一条p代码并存入代码区
     * @param type 代码类型
     * @param l 层差（如果有）
     * @param a 参数（如果有）
     */
    public void generateCode(pCodeType type, Integer l, Integer a){
        var code = new pCode(type, l, a);
        codes.add(code);
    }

    /**
     * @param addr 回填目标地址
     * @param val 回填值
     */
    public void backPatch(int addr, int val){
        var code = codes.get(addr);
        code.a = val;
        codes.set(addr, code);
    }

    /**
     * @param addrList 回填目标地址列表
     * @param val 回填值
     */
    public void backPatch(ArrayList<Integer> addrList, int val) {
        if(addrList.isEmpty()) return;
        for (var addr: addrList) {
            backPatch(addr, val);
        }
    }

    public int getNextCodeAddr() {
        return codes.size();
    }

    public void displayCode(){
        for(var code : codes){
            System.out.format("%-5s"+"\t"+"%-5d"+"\t"+"%-5d\r\n"
                    ,code.codeType, code.L, code.a);
        }
    }
}
