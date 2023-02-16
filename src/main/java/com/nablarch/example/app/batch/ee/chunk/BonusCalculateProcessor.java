package com.nablarch.example.app.batch.ee.chunk;

import com.nablarch.example.app.batch.ee.form.EmployeeForm;
import com.nablarch.example.app.entity.Bonus;

import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * 賞与計算を行う{@link ItemProcessor}実装クラス。
 *
 * @author Nabu Rakutaro
 */
@Dependent
@Named
public class BonusCalculateProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object item) {

        EmployeeForm form = (EmployeeForm) item;
        Bonus bonus = new Bonus();
        bonus.setEmployeeId(form.getEmployeeId());
        bonus.setPayments(calculateBonus(form));

        return bonus;
    }

    /**
     * 社員情報をもとに賞与計算を行う。
     *
     * @param form 社員情報Form
     * @return 賞与
     */
    private static Long calculateBonus(EmployeeForm form) {
        if (form.getFixedBonus() == null) {
            return form.getBasicSalary() * form.getBonusMagnification() / 100;
        } else {
            return form.getFixedBonus();
        }
    }
}
