# 南京航空航天大学 编译原理实验与课设

## PL0 文法

```text
一个PASCAL语言子集（PL/0）词法分析器的设计与实现
PL/0语言的BNF描述（扩充的巴克斯范式表示法）
<prog> → program <id>；<block>
<block> → [<condecl>][<vardecl>][<proc>]<body>
<condecl> → const <const>{,<const>};
<const> → <id>:=<integer>
<vardecl> → var <id>{,<id>};
<proc> → procedure <id>（[<id>{,<id>}]）;<block>{;<proc>}
<body> → begin <statement>{;<statement>}end
<statement> → <id> := <exp>
               |if <lexp> then <statement>[else <statement>]
               |while <lexp> do <statement>
               |call <id>（[<exp>{,<exp>}]）
               |<body>
               |read (<id>{，<id>})
               |write (<exp>{,<exp>})
<lexp> → <exp> <lop> <exp>|odd <exp>
<exp> → [+|-]<term>{<aop><term>}
<term> → <factor>{<mop><factor>}
<factor>→<id>|<integer>|(<exp>)
<lop> → =|<>|<|<=|>|>=
<aop> → +|-
<mop> → *|/
<id> → l{l|d}   （注：l表示字母）
<integer> → d{d}
注释：
<prog>：程序 ；<block>：块、程序体 ；<condecl>：常量说明 ；<const>：常量；
<vardecl>：变量说明 ；<proc>：分程序 ； <body>：复合语句 ；<statement>：语句；
<exp>：表达式 ；<lexp>：条件 ；<term>：项 ； <factor>：因子 ；<aop>：加法运算符；
<mop>：乘法运算符； <lop>：关系运算符
odd：判断表达式的奇偶性。
```

## 词法分析部分：
代码包package.lexer提供了编译器最基础的词法分析。Lexer为词法分析器主要的类，其中的main方法提供了测试接口。

**示例：**

词法分析前
```PASCAL
program ProgramTest;
begin
    a := -10
    b := 20
    c := a + b
    if c = 10 then
        call testFun1
    else
        call testFun2
    d = a * b
    read testFile
    write c
    while a <> b do
        a = a * a
end
```
词法分析后
```text
< PROGRAM program > < ID ProgramTest > < SPLIT ; > 
< BEGIN begin > 
< ID a > < ASSIGN := > < AOP - > < INTEGER 10 > 
< ID b > < ASSIGN := > < INTEGER 20 > 
< ID c > < ASSIGN := > < ID a > < AOP + > < ID b > 
< IF if > < ID c > < LOP = > < INTEGER 10 > < THEN then > 
< CALL call > < ID testFun1 > 
< ELSE else > 
< CALL call > < ID testFun2 > 
< ID d > < LOP = > < ID a > < MOP * > < ID b > 
< READ read > < ID testFile > 
< WRITE write > < ID c > 
< WHILE while > < ID a > < LOP <> > < ID b > < DO do > 
< ID a > < LOP = > < ID a > < MOP * > < ID a > 
< END end > 
```

## 测试说明
通过输入代码文本文件路径（IDEA默认为项目目录）进行代码的读取测试，例如本项目中根目录的code.txt文件