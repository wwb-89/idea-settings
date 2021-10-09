update t_activity_manager set menu= CONCAT(menu, ',notice,setting') where menu is not null and menu != '';
