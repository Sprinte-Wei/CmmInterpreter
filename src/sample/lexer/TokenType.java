package sample.lexer;

public enum TokenType {
    /* 算术运算符 + - * / ++ -- % */
    PLUS, MINUS, MULTIPLY, DIVIDE, AUTO_INCREMENT, AUTO_DECREMENT, MODULO,
    /* 逻辑运算符 > < == != >= <= && || ! */
    GREATER_THAN, LESS_THAN, EQUAL, NOT_EQUAL, GREAT_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,
    /* 位运算符 & | */
    BIT_AND, BIT_OR,
    /* 赋值运算符 = */
    ASSIGN,
    /* 控制语句保留字 read write while for if else switch case default break */
    READ, WRITE, WHILE, FOR, IF, ELSE, SWITCH, CASE, DEFAULT, BREAK,
    /* 数据类型保留字 int float double char bool true false */
    INT, DOUBLE, CHAR, BOOL, STRING,
    /* 函数返回类型保留字（包含以上数据类型保留字） void return */
    VOID, RETURN,
    /* 常量 int-number double-number character true false */
    NUMBER_INT, NUMBER_DOUBLE, CHARACTER, TRUE, FALSE, CONSTANT_STRING,
    /* 标识符（包括变量名和函数名） 由字母数字下划线组成（只能以字母开头，不能以下划线结尾） */
    IDENTIFIER,
    /* 分隔符 , ; ( ) { } : [ ]  */
    COMMA, SEMICOLON, LEFT_PARENTHESIS, RIGHT_PARENTHESIS, LEFT_BRACE, RIGHT_BRACE, COLON, LEFT_BRACKET, RIGHT_BRACKET,
    /* 单行注释 */
    SINGLE_LINE_NOTATION,
    /* 多行注释 */
    MULTIPLE_LINE_NOTATION,
    /* 结束符号 */
    END,
}
